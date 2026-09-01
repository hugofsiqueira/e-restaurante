package br.com.fiap.erestaurante.infrastructure.persistence.repository;

import br.com.fiap.erestaurante.domain.model.Usuario;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;
import br.com.fiap.erestaurante.infrastructure.persistence.mapper.UsuarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de saída — implementa a porta definida pelo domain usando JPA.
 *
 * DIP: o domain nunca conhece esta classe; conhece apenas UsuarioRepositoryPort.
 * Trocar o banco de dados significa criar outro adapter, sem tocar no domain ou application.
 */
@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

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
