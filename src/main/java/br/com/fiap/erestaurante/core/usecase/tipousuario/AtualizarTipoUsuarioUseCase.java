package br.com.fiap.erestaurante.core.usecase.tipousuario;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;

public interface AtualizarTipoUsuarioUseCase {

    record Command(String nome) {}

    TipoUsuario execute(Long id, Command command);
}
