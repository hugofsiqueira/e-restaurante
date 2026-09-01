package br.com.fiap.erestaurante.infrastructure.web.dto.request;

import br.com.fiap.erestaurante.domain.model.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para cadastro de um novo usuário")
public class CadastrarUsuarioRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150)
    @Schema(example = "João Silva")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Schema(example = "joao@email.com")
    private String email;

    @NotBlank(message = "Login é obrigatório")
    @Size(max = 100)
    @Schema(example = "joaosilva")
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    @Schema(example = "senha123")
    private String senha;

    @NotNull(message = "Tipo de usuário é obrigatório")
    @Schema(example = "CLIENTE", allowableValues = {"CLIENTE", "DONO_RESTAURANTE"})
    private TipoUsuario tipo;

    @Valid
    @NotNull(message = "Endereço é obrigatório")
    private EnderecoRequest endereco;
}
