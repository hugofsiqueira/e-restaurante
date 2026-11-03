package br.com.fiap.erestaurante.core.gateway;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;

import java.util.List;
import java.util.Optional;

public interface TipoUsuarioGateway {

    TipoUsuario salvar(TipoUsuario tipoUsuario);

    Optional<TipoUsuario> buscarPorId(Long id);

    List<TipoUsuario> buscarTodos();

    boolean existePorNome(String nome);

    void excluir(Long id);
}
