package br.com.fiap.erestaurante.infra.database.mapper;

import br.com.fiap.erestaurante.core.domain.Cliente;
import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.infra.database.entity.ClienteEntity;
import br.com.fiap.erestaurante.infra.database.entity.DonoRestauranteEntity;
import br.com.fiap.erestaurante.infra.database.entity.EnderecoEntity;
import br.com.fiap.erestaurante.infra.database.entity.TipoUsuarioEntity;
import br.com.fiap.erestaurante.infra.database.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct gera a implementação em tempo de compilação.
 */
@Mapper(componentModel = "spring")
public interface UsuarioPersistenceMapper {

    default UsuarioEntity toEntity(Usuario usuario) {
        if (usuario instanceof Cliente c) return clienteToEntity(c);
        if (usuario instanceof DonoRestaurante d) return donoToEntity(d);
        throw new IllegalArgumentException("Tipo de usuário desconhecido: " + usuario.getClass());
    }

    @Mapping(target = "dataUltimaAlteracao", ignore = true)
    ClienteEntity clienteToEntity(Cliente cliente);

    @Mapping(target = "dataUltimaAlteracao", ignore = true)
    DonoRestauranteEntity donoToEntity(DonoRestaurante dono);

    EnderecoEntity enderecoToEntity(Endereco endereco);

    TipoUsuarioEntity tipoToEntity(TipoUsuario tipoUsuario);

    TipoUsuario tipoToDomain(TipoUsuarioEntity entity);

    default Usuario toDomain(UsuarioEntity entity) {
        if (entity instanceof ClienteEntity c) return clienteToDomain(c);
        if (entity instanceof DonoRestauranteEntity d) return donoToDomain(d);
        throw new IllegalArgumentException("Tipo de entity desconhecido: " + entity.getClass());
    }

    Cliente clienteToDomain(ClienteEntity entity);

    DonoRestaurante donoToDomain(DonoRestauranteEntity entity);

    Endereco enderecoToDomain(EnderecoEntity entity);

    List<Usuario> toDomainList(List<UsuarioEntity> entities);
}
