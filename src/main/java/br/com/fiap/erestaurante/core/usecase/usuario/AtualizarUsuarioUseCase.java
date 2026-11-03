package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Usuario;

public interface AtualizarUsuarioUseCase {

    record Command(
            String nome,
            String email,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String cep
    ) {}

    Usuario execute(Long id, Command command);
}
