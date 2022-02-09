package de.rwth.idsg.steve.mq.kafka.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.mq.kafka.service.MessageIdService;
import de.rwth.idsg.steve.mq.message.OperationRequest;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.rw.CloudEventRWException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import static de.rwth.idsg.steve.utils.ValidationUtils.requireNotBlank;

/**
 * @author ralf.heese
 */
@Component
public class CloudEventMessageMapperImpl implements CloudEventMessageMapper {

    private final MessageIdService idGenerator;
    private final MessageTypeMapper typeMapper;
    private final URI producerId;
    private final ObjectMapper mapper;

    @Autowired
    public CloudEventMessageMapperImpl(MessageIdService idGenerator,
                                       MessageTypeMapper typeMapper,
                                       @Qualifier("kafkaProducerId") URI producerId,
                                       ObjectMapper mapper) {
        this.idGenerator = idGenerator;
        this.typeMapper = typeMapper;
        this.producerId = producerId;
        this.mapper = mapper;
    }

    @NotNull
    @Override
    public CloudEvent toEvent(@NotNull Object data) throws CloudEventRWException {
        try {
            byte[] bytes = mapper.writeValueAsBytes(data);

            return CloudEventBuilder.v1()
                                    .withId(idGenerator.next().toString())
                                    .withType(typeMapper.getType(data))
                                    .withSource(producerId)
                                    .withData(BytesCloudEventData.wrap(bytes))
                                    .build();
        } catch (JsonProcessingException e) {
            throw CloudEventRWException.newDataConversion(e, data.getClass().toString(), "byte[]");
        }
    }

    @Override
    public <T> @NotNull T fromEvent(@NotNull CloudEvent event, @NotNull Class<T> clazz) {
        CloudEventData data = event.getData();
        if (data == null) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new SteveException("Cannot create instance of class from cloud event: " + clazz.getName(), e);
            }
        }

        try {
            return mapper.readValue(data.toBytes(), clazz);
        } catch (IOException e) {
            throw CloudEventRWException.newDataConversion(e, "byte[]", "Map<String,String");
        }
    }

    @Override
    public @NotNull Object fromEvent(@NotNull CloudEvent event) {
        if (StringUtils.isBlank(event.getType())) {
            throw new IllegalArgumentException("Cloud event without a type");
        }

        try {
            Class<?> clazz = typeMapper.getClass(event.getType());
            Object typeInstance = clazz.getConstructor()
                                       .newInstance();

            Object message = event.getData() == null ? typeInstance : mapper.readValue(event.getData().toBytes(), typeInstance.getClass());
            if (message instanceof OperationRequest) {
                ((OperationRequest) message).setRequestId(requireNotBlank(event.getId(), "Cloud event must have a non-blank ID"));
            }

            return message;
        } catch (ClassNotFoundException e) {
            throw new SteveException("Unsupported type of a cloud event: " + event.getType(), e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new SteveException("Cannot instantiate type of a cloud event: " + event.getType(), e);
        } catch (IOException e) {
            throw CloudEventRWException.newDataConversion(e, "byte[]", "Map<String,String");
        }
    }
}
