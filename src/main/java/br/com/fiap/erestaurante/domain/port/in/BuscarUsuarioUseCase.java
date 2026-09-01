package br.com.fiap.erestaurante.domain.port.in;

import br.com.fiap.erestaurante.domain.model.Usuario;

import java.util.List;

/**
 * Porta de entrada — consultas de usuário.
 * ISP: agrupa operações de leitura relacionadas sem misturar com escrita.
 */
public interface BuscarUsuarioUseCase {

    Usuario buscarPorId(Long id);

    List<Usuario> buscarPorNome(String nome);
}
