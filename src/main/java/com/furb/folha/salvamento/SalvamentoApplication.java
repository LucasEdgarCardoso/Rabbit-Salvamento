package com.furb.folha.salvamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SalvamentoApplication {

    public static void main(String[] args) {
        System.out.println("=== SERVIÇO DE SALVAMENTO INICIADO ===");
        System.out.println("Aguardando dados para salvar...");
        SpringApplication.run(SalvamentoApplication.class, args);
    }
}
