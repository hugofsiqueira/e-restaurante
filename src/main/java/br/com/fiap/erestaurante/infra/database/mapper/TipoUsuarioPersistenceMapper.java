package br.com.fiap.erestaurante.infra.database.mapper;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.infra.database.entity.TipoUsuarioEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TipoUsuarioPersistenceMapper {

    TipoUsuarioEntity toEntity(TipoUsuario tipoUsuario);

    TipoUsuario toDomain(TipoUsuarioEntity entity);

    List<TipoUsuario> toDomainList(List<TipoUsuarioEntity> entities);
}
