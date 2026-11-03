package br.com.fiap.erestaurante.infra.database.jpa;

import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import br.com.fiap.erestaurante.infra.database.mapper.UsuarioPersistenceMapper;
import br.com.fiap.erestaurante.infra.database.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementação do gateway de saída usando Spring Data JPA.
 * DIP: o core nunca conhece esta classe; conhece apenas UsuarioGateway.
 */
@Component
@RequiredArgsConstructor
public class UsuarioJpaGateway implements UsuarioGateway {

    private final UsuarioRepository repository;
    private final UsuarioPersistenceMapper mapper;

    @Override
    public Usuario salvar(Usuario usuario) {
        var entity = mapper.toEntity(usuario);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return repository.findByLogin(login).map(mapper::toDomain);
    }

    @Override
    public List<Usuario> buscarPorNome(String nome) {
        return mapper.toDomainList(repository.findByNomeContainingIgnoreCase(nome));
    }

    @Override
    public boolean existePorEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
