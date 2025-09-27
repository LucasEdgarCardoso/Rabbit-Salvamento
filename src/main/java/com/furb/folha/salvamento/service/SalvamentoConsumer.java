package com.furb.folha.salvamento.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class SalvamentoConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final List<CalculoSalvo> bancoDados; // Simulando banco em memoria

    public SalvamentoConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.bancoDados = new ArrayList<>();
    }

    @RabbitListener(queues = "q.salvar.calculo")
    public void salvarCalculoNoBanco(String mensagem) {
        try {
            System.out.println("\n[SALVAMENTO] Recebido para salvar: " + mensagem);

            // Parse da mensagem JSON
            JsonNode json = objectMapper.readTree(mensagem);
            String clienteId = json.get("clienteId").asText();
            double salarioBruto = json.get("salarioBruto").asDouble();
            double inss = json.get("inss").asDouble();
            double irrf = json.get("irrf").asDouble(); // IRRF = Imposto de Renda Retido na Fonte
            double salarioLiquido = json.get("salarioLiquido").asDouble();

            System.out.println("[SALVAMENTO] Cliente ID: " + clienteId);
            System.out.println("[SALVAMENTO] Valores - Bruto: R$ " + String.format(Locale.US, "%.2f", salarioBruto) +
                    " | INSS: R$ " + String.format(Locale.US, "%.2f", inss) +
                    " | IRRF: R$ " + String.format(Locale.US, "%.2f", irrf) +
                    " | Liquido: R$ " + String.format(Locale.US, "%.2f", salarioLiquido));
            System.out.println("[SALVAMENTO] Salvando no banco de dados...");

            // Simular salvamento no banco
            Thread.sleep(1500); // Simula tempo de I/O do banco

            CalculoSalvo calculo = new CalculoSalvo();
            calculo.id = bancoDados.size() + 1;
            calculo.clienteId = clienteId;
            calculo.salarioBruto = salarioBruto;
            calculo.inss = inss;
            calculo.irrf = irrf;
            calculo.salarioLiquido = salarioLiquido;
            calculo.dataProcessamento = LocalDateTime.now();

            bancoDados.add(calculo);

            System.out.println("[SALVAMENTO] Calculo salvo com ID: " + calculo.id);
            System.out.println("[SALVAMENTO] Total de registros no banco: " + bancoDados.size());

            // CORREÇÃO: Usar Locale.US para garantir formatação correta do JSON
            String resultadoFinalJson = String.format(Locale.US,
                    "{\"clienteId\":\"%s\", \"id\":%d, \"salarioBruto\":%.2f, \"inss\":%.2f, \"irrf\":%.2f, \"salarioLiquido\":%.2f, \"status\":\"salvo\", \"dataProcessamento\":\"%s\"}",
                    clienteId,
                    calculo.id,
                    salarioBruto,
                    inss,
                    irrf,
                    salarioLiquido,
                    calculo.dataProcessamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );

            // Publicar evento final para o cliente
            rabbitTemplate.convertAndSend("eventos.topic", clienteId, resultadoFinalJson);
            System.out.println("[SALVAMENTO] Notificacao final enviada ao cliente com routing key: " + clienteId);

            // Exibir resumo do banco
            exibirResumoBanco();

        } catch (Exception e) {
            System.err.println("[ERRO] Falha no salvamento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void exibirResumoBanco() {
        System.out.println("\n=== RESUMO DO BANCO DE DADOS ===");
        for (CalculoSalvo calc : bancoDados) {
            System.out.printf(Locale.US, "ID: %d | Cliente: %s | Liquido: R$ %.2f | Data: %s%n",
                    calc.id,
                    calc.clienteId,
                    calc.salarioLiquido,
                    calc.dataProcessamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );
        }
        System.out.println("==============================\n");
    }

    static class CalculoSalvo {
        int id;
        String clienteId;
        double salarioBruto;
        double inss;
        double irrf; // IRRF = Imposto de Renda Retido na Fonte
        double salarioLiquido;
        LocalDateTime dataProcessamento;
    }
}