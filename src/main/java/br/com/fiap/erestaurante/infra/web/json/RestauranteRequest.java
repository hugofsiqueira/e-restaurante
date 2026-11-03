package br.com.fiap.erestaurante.infra.web.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados do restaurante")
public class RestauranteRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200)
    private String nome;

    @NotNull(message = "ID do dono é obrigatório")
    private Long donoId;

    @NotBlank(message = "Tipo de cozinha é obrigatório")
    @Size(max = 100)
    private String tipoCozinha;

    @Valid
    @NotNull(message = "Endereço é obrigatório")
    private EnderecoRequest endereco;

    @Valid
    @NotNull(message = "Horário de funcionamento é obrigatório")
    private HorarioFuncionamentoRequest horarioFuncionamento;
}
