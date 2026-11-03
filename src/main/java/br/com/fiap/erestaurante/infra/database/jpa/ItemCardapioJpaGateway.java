package br.com.fiap.erestaurante.infra.database.jpa;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.exception.ItemCardapioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.ItemCardapioGateway;
import br.com.fiap.erestaurante.infra.database.mapper.ItemCardapioPersistenceMapper;
import br.com.fiap.erestaurante.infra.database.repository.ItemCardapioRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ItemCardapioJpaGateway implements ItemCardapioGateway {

    private final ItemCardapioRepository repository;
    private final ItemCardapioPersistenceMapper mapper;


    @Override
    public ItemCardapio salvar(ItemCardapio item) {
        var entity = mapper.toEntity(item);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<ItemCardapio> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ItemCardapio> buscarPorRestaurante(Long restauranteId) {
        return mapper.toDomainList(repository.findByRestauranteId(restauranteId));
    }

    @Override
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new ItemCardapioNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }
}
