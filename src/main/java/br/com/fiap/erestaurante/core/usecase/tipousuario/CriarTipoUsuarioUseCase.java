package br.com.fiap.erestaurante.core.usecase.tipousuario;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;

public interface CriarTipoUsuarioUseCase {

    record Command(String nome) {}

    TipoUsuario execute(Command command);
}
