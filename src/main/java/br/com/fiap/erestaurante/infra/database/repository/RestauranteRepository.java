package br.com.fiap.erestaurante.infra.database.repository;

import br.com.fiap.erestaurante.infra.database.entity.RestauranteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestauranteRepository extends JpaRepository<RestauranteEntity, Long> {

    List<RestauranteEntity> findByNomeContainingIgnoreCase(String nome);
}
