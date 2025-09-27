package com.furb.folha.salvamento;

import com.furb.folha.salvamento.dto.ClienteRec;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SalvamentoApplication implements CommandLineRunner {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SalvamentoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // O Salvamento vai consumir a fila de cálculos
        System.out.println("Aguardando o cálculo para salvar...");
    }

    @RabbitListener(queues = "q.salvar.calculo")
    public void salvarCalculo(ClienteRec clienteRec) {
        System.out.println("Salvando cálculo: " + clienteRec);

        // Simulando o salvamento no banco de dados
        String clienteId = "123";  // Exemplo de ID do cliente
        String resultado = "Cálculo do cliente " + clienteId + " salvo com sucesso!";

        // Envia a notificação para o cliente
        amqpTemplate.convertAndSend("eventos.topic", "cliente.eventos." + clienteId, resultado);
    }
}
