package com.ms.cadastro.config;

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


//Configuração do RabbitMQ: declara a exchange, fila e conversor JSON.
@Configuration
public class RabbitMQConfig {

    // Valores carregados do application.properties
    @Value("${broker.exchange.name}")
    private String exchange;

    @Value("${broker.queue.pet_request}")
    private String petRequestQueue;

    @Value("${broker.routingkey.pet_info_request}")
    private String petInfoRequestRoutingKey;

 
    //Cria a exchange principal do tipo direct, usada para rotear mensagens com base na routing key.
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchange);
    }

    
    //Declara a fila que o microsserviço de cadastro escuta: pet.requests
    @Bean
    public Queue petRequestQueue() {
        // ⚠️ Esta fila só deve ser declarada no serviço de CADASTRO
        return new Queue(petRequestQueue, true); // durable = true
    }

    
    //Cria o binding entre a fila pet.requests e a exchange, usando a routing key pet.info.request
    @Bean
    public Binding bindingPetInfoRequest() {
        return BindingBuilder
                .bind(petRequestQueue())
                .to(directExchange())
                .with(petInfoRequestRoutingKey);
    }

 
    //Conversor JSON com suporte para datas (LocalDate).
    //Garante que datas sejam serializadas/deserializadas corretamente no formato ISO (ex: 2025-04-11)
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Suporte para tipos java.time
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Datas como texto ISO

        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
