package br.com.fiap.erestaurante.infra.database.repository;

import br.com.fiap.erestaurante.infra.database.entity.ItemCardapioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemCardapioRepository extends JpaRepository<ItemCardapioEntity, Long> {

    List<ItemCardapioEntity> findByRestauranteId(Long restauranteId);
}
