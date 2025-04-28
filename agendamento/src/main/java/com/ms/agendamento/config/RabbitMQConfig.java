package com.ms.agendamento.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    // Filas escutadas por este serviço
    @Value("${broker.queue.pet_created}")
    private String petCreatedQueue;

    @Value("${broker.queue.pet_updated}")
    private String petUpdatedQueue;

    @Value("${broker.queue.pet_responses}")
    private String petResponsesQueue;

    @Value("${broker.queue.appointment_confirmed_bymail}")
    private String appointmentConfirmedByMailQueue;

    @Value("${broker.queue.appointment_cancelled_bymail}")
    private String appointmentCancelledByMailQueue;

    // Routing keys escutadas por este serviço
    @Value("${broker.routingkey.pet_created}")
    private String petCreatedRoutingKey;

    @Value("${broker.routingkey.pet_updated}")
    private String petUpdatedRoutingKey;

    @Value("${broker.routingkey.pet_info_response}")
    private String petInfoResponseRoutingKey;

    @Value("${broker.routingkey.appointment_confirmed}")
    private String appointmentConfirmedRoutingKey;

    @Value("${broker.routingkey.appointment_cancelled_request}")
    private String appointmentCancelledRequestRoutingKey;

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue petCreatedQueue() {
        return new Queue(petCreatedQueue, true);
    }

    @Bean
    public Queue petUpdatedQueue() {
        return new Queue(petUpdatedQueue, true);
    }

    @Bean
    public Queue petResponsesQueue() {
        return new Queue(petResponsesQueue, true);
    }

    @Bean
    public Queue appointmentConfirmedByMailQueue() {
        return new Queue(appointmentConfirmedByMailQueue, true);
    }

    @Bean
    public Queue appointmentCancelledByMailQueue() {
        return new Queue(appointmentCancelledByMailQueue, true);
    }

    @Bean
    public Binding bindingPetCreated() {
        return BindingBuilder.bind(petCreatedQueue()).to(directExchange()).with(petCreatedRoutingKey);
    }

    @Bean
    public Binding bindingPetUpdated() {
        return BindingBuilder.bind(petUpdatedQueue()).to(directExchange()).with(petUpdatedRoutingKey);
    }

    @Bean
    public Binding bindingPetResponses() {
        return BindingBuilder.bind(petResponsesQueue()).to(directExchange()).with(petInfoResponseRoutingKey);
    }

    @Bean
    public Binding bindingAppointmentConfirmed() {
        return BindingBuilder.bind(appointmentConfirmedByMailQueue()).to(directExchange()).with(appointmentConfirmedRoutingKey);
    }

    @Bean
    public Binding bindingAppointmentCancelled() {
        return BindingBuilder.bind(appointmentCancelledByMailQueue()).to(directExchange()).with(appointmentCancelledRequestRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
