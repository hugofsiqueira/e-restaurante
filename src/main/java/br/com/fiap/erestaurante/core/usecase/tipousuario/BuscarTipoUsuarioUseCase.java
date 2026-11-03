package br.com.fiap.erestaurante.core.usecase.tipousuario;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;

import java.util.List;

public interface BuscarTipoUsuarioUseCase {

    TipoUsuario buscarPorId(Long id);

    List<TipoUsuario> buscarTodos();
}
