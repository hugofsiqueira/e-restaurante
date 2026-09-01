package br.com.fiap.erestaurante.infrastructure.persistence.mapper;

import br.com.fiap.erestaurante.domain.model.Cliente;
import br.com.fiap.erestaurante.domain.model.DonoRestaurante;
import br.com.fiap.erestaurante.domain.model.Endereco;
import br.com.fiap.erestaurante.domain.model.Usuario;
import br.com.fiap.erestaurante.infrastructure.persistence.entity.ClienteEntity;
import br.com.fiap.erestaurante.infrastructure.persistence.entity.DonoRestauranteEntity;
import br.com.fiap.erestaurante.infrastructure.persistence.entity.EnderecoEntity;
import br.com.fiap.erestaurante.infrastructure.persistence.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct gera a implementação em tempo de compilação — sem reflection em runtime.
 * componentModel = "spring" registra o mapper como bean Spring automaticamente.
 */
@Mapper(componentModel = "spring")
public interface UsuarioPersistenceMapper {

    // ── Domain → Entity ──────────────────────────────────────────────

    default UsuarioEntity toEntity(Usuario usuario) {
        if (usuario instanceof Cliente c) return clienteToEntity(c);
        if (usuario instanceof DonoRestaurante d) return donoToEntity(d);
        throw new IllegalArgumentException("Tipo de usuário desconhecido: " + usuario.getClass());
    }

    @Mapping(target = "dataUltimaAlteracao", ignore = true) // gerenciado pelo @PrePersist/@PreUpdate
    ClienteEntity clienteToEntity(Cliente cliente);

    @Mapping(target = "dataUltimaAlteracao", ignore = true)
    DonoRestauranteEntity donoToEntity(DonoRestaurante dono);

    EnderecoEntity enderecoToEntity(Endereco endereco);

    // ── Entity → Domain ──────────────────────────────────────────────

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
