package br.com.fiap.erestaurante.domain.port.in;

import br.com.fiap.erestaurante.domain.model.TipoUsuario;
import br.com.fiap.erestaurante.domain.model.Usuario;

/**
 * Porta de entrada — ISP: contrato isolado apenas para cadastro.
 * O controller depende desta interface, nunca da implementação concreta.
 */
public interface CadastrarUsuarioUseCase {

    record Command(
            String nome,
            String email,
            String login,
            String senha,
            TipoUsuario tipo,
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
