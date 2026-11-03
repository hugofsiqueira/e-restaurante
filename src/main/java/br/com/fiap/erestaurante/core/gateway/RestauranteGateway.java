package br.com.fiap.erestaurante.core.gateway;

import br.com.fiap.erestaurante.core.domain.Restaurante;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de saída — contrato de persistência definido pelo core.
 * DIP: o core define esta interface; a infraestrutura (JPA) a implementa.
 */
public interface RestauranteGateway {

    Restaurante salvar(Restaurante restaurante);

    Optional<Restaurante> buscarPorId(Long id);

    List<Restaurante> buscarPorNome(String nome);

    void excluir(Long id);
}
