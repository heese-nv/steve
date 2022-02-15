package de.rwth.idsg.steve.api.controller;

import de.rwth.idsg.steve.api.contract.ChargePointOperationContract;
import de.rwth.idsg.steve.api.contract.ChargePointOperationResponseContract;
import de.rwth.idsg.steve.mq.kafka.service.KafkaChargePointProducerService;
import de.rwth.idsg.steve.mq.kafka.service.MessageIdService;
import de.rwth.idsg.steve.mq.message.ChargePointOperationRequest;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.rwth.idsg.steve.utils.ValidationUtils.requireNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


/**
 * @author ralf.heese
 */
@RestController
@OpenAPIDefinition(info = @Info(title = "Charge Point", version = "1.0"), tags = @Tag(name = "Operations"))
@RequestMapping(path = ApiPaths.API_V1)
public class ChargePointApiController {

    private final KafkaChargePointProducerService service;
    private final MessageIdService messageIdService;

    public ChargePointApiController(KafkaChargePointProducerService service, MessageIdService messageIdService) {
        this.service = service;
        this.messageIdService = messageIdService;
    }

    @PostMapping(path = "charge_points/send")
    public ResponseEntity<ChargePointOperationResponseContract> test(@RequestBody() ChargePointOperationContract operation) {
        requireNotBlank(operation.getAction(), "action required");
        ChargePointOperationRequest message;

        String messageId = isNotBlank(operation.getMessageId()) ? operation.getMessageId() : messageIdService.next().toString();
        switch (operation.getAction()) {
            default:
                message = ChargePointOperationRequest.builder()
                                                     .messageId(messageId)
                                                     .chargePointId(operation.getChargePointId())
                                                     .action(operation.getAction())
                                                     .build();
        }

        String requestId = service.send(message);

        ChargePointOperationResponseContract response = ChargePointOperationResponseContract.builder()
                                                                                            .messageId(requestId)
                                                                                            .chargePointId(operation.getChargePointId())
                                                                                            .status("success")
                                                                                            .build();
        return ResponseEntity.ok(response);
    }
}
