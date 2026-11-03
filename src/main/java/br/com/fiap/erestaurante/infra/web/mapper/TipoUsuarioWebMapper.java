package br.com.fiap.erestaurante.infra.web.mapper;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.infra.web.json.TipoUsuarioResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TipoUsuarioWebMapper {

    TipoUsuarioResponse toResponse(TipoUsuario tipoUsuario);

    List<TipoUsuarioResponse> toResponseList(List<TipoUsuario> tipos);
}
