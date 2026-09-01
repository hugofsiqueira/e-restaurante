package br.com.fiap.erestaurante.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para atualização do usuário (senha não é alterada aqui)")
public class AtualizarUsuarioRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150)
    @Schema(example = "João da Silva")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Schema(example = "joao.silva@email.com")
    private String email;

    @Valid
    @NotNull(message = "Endereço é obrigatório")
    private EnderecoRequest endereco;
}
