package br.com.fiap.erestaurante.infra.database.repository;

import br.com.fiap.erestaurante.infra.database.entity.TipoUsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuarioEntity, Long> {

    boolean existsByNomeIgnoreCase(String nome);
}
