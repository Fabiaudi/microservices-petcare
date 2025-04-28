package com.ms.notificacao.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitMQConfig {

    @Value("${broker.exchange.name}")
    private String exchange;

    @Value("${broker.queue.pet_created}")
    private String petCreatedQueue;

    @Value("${broker.queue.appointment_created}")
    private String appointmentCreatedQueue;

    @Value("${broker.queue.appointment_updated}")
    private String appointmentUpdatedQueue;

    @Value("${broker.queue.appointment_cancelled}")
    private String appointmentCancelledQueue;

    @Value("${broker.routingkey.pet_created}")
    private String petCreatedRoutingKey;

    @Value("${broker.routingkey.appointment_created}")
    private String appointmentCreatedRoutingKey;

    @Value("${broker.routingkey.appointment_updated}")
    private String appointmentUpdatedRoutingKey;

    @Value("${broker.routingkey.appointment_cancelled_send}")
    private String appointmentCancelledRoutingKey;

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue petCreatedQueue() {
        return new Queue(petCreatedQueue, true);
    }

    @Bean
    public Queue appointmentCreatedQueue() {
        return new Queue(appointmentCreatedQueue, true);
    }

    @Bean
    public Queue appointmentUpdatedQueue() {
        return new Queue(appointmentUpdatedQueue, true);
    }

    @Bean
    public Queue appointmentCancelledQueue() {
        return new Queue(appointmentCancelledQueue, true);
    }

    @Bean
    public Binding bindingPetCreated() {
        return BindingBuilder.bind(petCreatedQueue()).to(directExchange()).with(petCreatedRoutingKey);
    }

    @Bean
    public Binding bindingAppointmentCreated() {
        return BindingBuilder.bind(appointmentCreatedQueue()).to(directExchange()).with(appointmentCreatedRoutingKey);
    }

    @Bean
    public Binding bindingAppointmentUpdated() {
        return BindingBuilder.bind(appointmentUpdatedQueue()).to(directExchange()).with(appointmentUpdatedRoutingKey);
    }

    @Bean
    public Binding bindingAppointmentCancelled() {
        return BindingBuilder.bind(appointmentCancelledQueue())
                .to(directExchange())
                .with(appointmentCancelledRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }
}
