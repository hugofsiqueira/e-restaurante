package br.com.fiap.erestaurante.domain.port.in;

import br.com.fiap.erestaurante.domain.model.Usuario;

/**
 * Porta de entrada — atualização de dados do usuário (exceto senha).
 * ISP: separado de TrocarSenhaUseCase por ter regras e validações distintas.
 */
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
