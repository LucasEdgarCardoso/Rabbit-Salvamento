package com.furb.folha.salvamento.service;

import com.furb.folha.salvamento.dto.HoleriteRec;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalvamentoConsumer {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RabbitListener(queues = "q.salvar.calculo")
    public void salvarCalculo(HoleriteRec holeriteRec) {
        System.out.println("Salvando cálculo: " + holeriteRec.id());
        // Lógica para salvar no banco de dados

        // Notificar o cliente sobre a conclusão do processo
        amqpTemplate.convertAndSend("eventos.topic", "cliente.eventos." + holeriteRec.id(), holeriteRec);
    }
}
