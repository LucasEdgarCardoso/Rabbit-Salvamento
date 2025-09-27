package com.furb.folha.salvamento.dto;

import java.util.UUID;

public record ClienteRec(
        UUID id,
        double salario
) {}
