package br.com.fiap.erestaurante.infra.web.json;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioFuncionamentoResponse {

    private LocalTime abertura;
    private LocalTime fechamento;
}
