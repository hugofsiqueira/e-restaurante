package br.com.fiap.erestaurante.infra.database.jpa;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import br.com.fiap.erestaurante.infra.database.mapper.TipoUsuarioPersistenceMapper;
import br.com.fiap.erestaurante.infra.database.repository.TipoUsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TipoUsuarioJpaGateway implements TipoUsuarioGateway {

    private final TipoUsuarioRepository repository;
    private final TipoUsuarioPersistenceMapper mapper;


    @Override
    public TipoUsuario salvar(TipoUsuario tipoUsuario) {
        var entity = mapper.toEntity(tipoUsuario);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<TipoUsuario> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TipoUsuario> buscarTodos() {
        return mapper.toDomainList(repository.findAll());
    }

    @Override
    public boolean existePorNome(String nome) {
        return repository.existsByNomeIgnoreCase(nome);
    }

    @Override
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new TipoUsuarioNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }
}
