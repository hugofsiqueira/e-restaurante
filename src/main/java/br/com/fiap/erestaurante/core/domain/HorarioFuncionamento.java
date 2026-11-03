package br.com.fiap.erestaurante.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

/**
 * Value Object — imutável, sem identidade própria.
 * Representa o horário de funcionamento de um restaurante.
 */
@Getter
@AllArgsConstructor
@Builder
public class HorarioFuncionamento {

    private final LocalTime abertura;
    private final LocalTime fechamento;
}
