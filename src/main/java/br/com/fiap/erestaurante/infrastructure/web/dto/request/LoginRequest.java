package br.com.fiap.erestaurante.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Credenciais de autenticação")
public class LoginRequest {

    @NotBlank(message = "Login é obrigatório")
    @Schema(example = "joaosilva")
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(example = "senha123")
    private String senha;
}
