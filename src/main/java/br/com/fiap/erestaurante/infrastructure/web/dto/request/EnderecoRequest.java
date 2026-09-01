package br.com.fiap.erestaurante.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EnderecoRequest {

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200)
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 20)
    private String numero;

    @Size(max = 100)
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (sigla UF)")
    private String estado;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
    private String cep;
}
