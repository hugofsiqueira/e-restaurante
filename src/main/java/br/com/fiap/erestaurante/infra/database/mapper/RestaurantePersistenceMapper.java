package br.com.fiap.erestaurante.infra.database.mapper;

import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.HorarioFuncionamento;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.infra.database.entity.DonoRestauranteEntity;
import br.com.fiap.erestaurante.infra.database.entity.HorarioFuncionamentoEntity;
import br.com.fiap.erestaurante.infra.database.entity.RestauranteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestaurantePersistenceMapper {

    @Mapping(source = "dono.id", target = "dono.id")
    RestauranteEntity toEntity(Restaurante restaurante);

    Restaurante toDomain(RestauranteEntity entity);

    HorarioFuncionamentoEntity toEntity(HorarioFuncionamento horario);

    HorarioFuncionamento toDomain(HorarioFuncionamentoEntity entity);

    List<Restaurante> toDomainList(List<RestauranteEntity> entities);

    default DonoRestauranteEntity donoToEntity(DonoRestaurante dono) {
        if (dono == null) return null;
        var entity = new DonoRestauranteEntity();
        entity.setId(dono.getId());
        return entity;
    }
}
