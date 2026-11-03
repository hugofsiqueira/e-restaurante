package br.com.fiap.erestaurante.infra.database.mapper;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.infra.database.entity.ItemCardapioEntity;
import br.com.fiap.erestaurante.infra.database.entity.RestauranteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemCardapioPersistenceMapper {

    @Mapping(source = "restaurante.id", target = "restaurante.id")
    ItemCardapioEntity toEntity(ItemCardapio item);

    ItemCardapio toDomain(ItemCardapioEntity entity);

    List<ItemCardapio> toDomainList(List<ItemCardapioEntity> entities);

    default RestauranteEntity restauranteToEntity(Restaurante restaurante) {
        if (restaurante == null) return null;
        var entity = new RestauranteEntity();
        entity.setId(restaurante.getId());
        return entity;
    }
}
