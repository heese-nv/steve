package de.rwth.idsg.steve.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

import java.util.Arrays;
import java.util.Properties;
import java.util.stream.StreamSupport;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;

/**
 * @author ralf.heese
 */
@Configuration
@PropertySource(value = "classpath:kafka.properties", ignoreResourceNotFound = true)
public class KafkaConfiguration {

    private final Environment env;

    public KafkaConfiguration(Environment env) {
        this.env = env;
    }

    /**
     * Configuration of Kafka.
     *
     * As we are not using Spring Boot, KafkaProperties is not populated automatically and not available as a wireable bean. Thus,
     * we have to populate it manually.
     */
    @Bean
    public KafkaProperties consumerConfig() {
        Properties properties = getProperties();
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(source);

        return binder.bind("spring.kafka", KafkaProperties.class).get();
    }

    /**
     * SteVe defines an object mapper instance in {@link JsonObjectMapper} which is used to transfer data between
     * charge points and SteVe. We define here an object mapper specific for exchanging data with Kafka. This ensures
     * that the configurations of the object mappers do not interfere with each other.
     *
     * @return object mapper
     */
    @Bean(name = "KafkaObjectMapper")
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Render as ISO-8601/UTC
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(FAIL_ON_NULL_FOR_PRIMITIVES, true);
        mapper.configure(WRITE_BIGDECIMAL_AS_PLAIN, true);


        return mapper;
    }

    /**
     * @return properties of the environment.
     */
    private Properties getProperties() {
        Properties props = new Properties();
        MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
        StreamSupport.stream(propSrcs.spliterator(), false)
                     .filter(ps -> ps instanceof EnumerablePropertySource)
                     .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                     .flatMap(Arrays::stream)
                     .forEach(propName -> props.setProperty(propName, env.getProperty(propName)));
        return props;
    }
}
