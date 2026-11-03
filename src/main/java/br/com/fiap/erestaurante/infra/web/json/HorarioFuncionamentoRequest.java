package br.com.fiap.erestaurante.infra.web.json;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioFuncionamentoRequest {

    @NotNull(message = "Horário de abertura é obrigatório")
    private LocalTime abertura;

    @NotNull(message = "Horário de fechamento é obrigatório")
    private LocalTime fechamento;
}
