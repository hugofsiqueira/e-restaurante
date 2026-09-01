package br.com.fiap.erestaurante.infrastructure.persistence.repository;

import br.com.fiap.erestaurante.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    Optional<UsuarioEntity> findByLogin(String login);

    List<UsuarioEntity> findByNomeContainingIgnoreCase(String nome);

    boolean existsByEmail(String email);
}
