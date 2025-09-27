package com.furb.folha.salvamento.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SalvamentoConfig {

    // Fila específica para salvar os cálculos processados
    @Bean
    public Queue queueSalvarCalculo() {
        return new Queue("q.salvar.calculo", true);  // Fila específica para o projeto de salvamento
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("eventos.topic");
    }

    // Binding para consumir a fila de cálculos
    @Bean
    public Binding bindingSalvarCalculo(Queue queueSalvarCalculo, TopicExchange exchange) {
        return BindingBuilder.bind(queueSalvarCalculo).to(exchange).with("salvar.calculo");
    }
}
