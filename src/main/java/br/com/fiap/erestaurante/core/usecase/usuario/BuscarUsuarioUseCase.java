package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Usuario;

import java.util.List;

public interface BuscarUsuarioUseCase {

    Usuario buscarPorId(Long id);

    List<Usuario> buscarPorNome(String nome);
}
