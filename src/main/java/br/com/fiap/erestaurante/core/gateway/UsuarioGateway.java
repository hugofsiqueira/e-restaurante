package br.com.fiap.erestaurante.core.gateway;

import br.com.fiap.erestaurante.core.domain.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de saída — contrato de persistência definido pelo core.
 * DIP: o core define esta interface; a infraestrutura (JPA) a implementa.
 */
public interface UsuarioGateway {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorLogin(String login);

    List<Usuario> buscarPorNome(String nome);

    boolean existePorEmail(String email);

    void excluir(Long id);
}
