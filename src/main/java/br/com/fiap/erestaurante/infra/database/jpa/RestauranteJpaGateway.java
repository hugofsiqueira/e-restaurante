package br.com.fiap.erestaurante.infra.database.jpa;

import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.RestauranteNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
import br.com.fiap.erestaurante.infra.database.mapper.RestaurantePersistenceMapper;
import br.com.fiap.erestaurante.infra.database.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RestauranteJpaGateway implements RestauranteGateway {

    private final RestauranteRepository repository;
    private final RestaurantePersistenceMapper mapper;


    @Override
    public Restaurante salvar(Restaurante restaurante) {
        var entity = mapper.toEntity(restaurante);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Restaurante> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Restaurante> buscarPorNome(String nome) {
        return mapper.toDomainList(repository.findByNomeContainingIgnoreCase(nome));
    }

    @Override
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RestauranteNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }
}
