package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Usuario;

public interface CadastrarUsuarioUseCase {

    record Command(
            String nome,
            String email,
            String login,
            String senha,
            Long tipoUsuarioId,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String cep
    ) {}

    Usuario execute(Command command);
}
