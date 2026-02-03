package com.example.vidaplena.config;

import com.example.vidaplena.domain.dto.event.AppointmentEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração do Apache Kafka.
 * 
 * <p>
 * Define beans para produtores e consumidores Kafka com serialização JSON.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Configuration
@EnableKafka
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9093}")
    private String bootstrapServers;

    /**
     * Configuração do produtor Kafka.
     */
    @Bean
    @NonNull
    public ProducerFactory<String, AppointmentEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * ProducerFactory genérico para objetos.
     */
    @Bean
    @NonNull
    public ProducerFactory<String, Object> genericProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * Template Kafka genérico para envio de qualquer tipo de mensagem.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(genericProducerFactory());
    }

    /**
     * Template Kafka específico para AppointmentEvent.
     */
    @Bean
    public KafkaTemplate<String, AppointmentEvent> appointmentEventKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Configuração do consumidor Kafka.
     */
    @Bean
    @NonNull
    public ConsumerFactory<String, AppointmentEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "vidaplena-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.vidaplena.domain.dto.event");
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, AppointmentEvent.class.getName());
        return new DefaultKafkaConsumerFactory<>(config);
    }

    /**
     * Factory para listeners Kafka.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AppointmentEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AppointmentEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
