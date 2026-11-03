package br.com.fiap.erestaurante.core.usecase.itemcardapio;

import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.RestauranteNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.ItemCardapioGateway;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdicionarItemCardapioUseCaseImplTest {

    @Mock
    private ItemCardapioGateway gateway;

    @Mock
    private RestauranteGateway restauranteGateway;

    private AdicionarItemCardapioUseCaseImpl useCase;

    private Restaurante restaurante;
    private AdicionarItemCardapioUseCase.Command command;

    @BeforeEach
    void setUp() {
        useCase = new AdicionarItemCardapioUseCaseImpl(gateway, restauranteGateway);

        restaurante = Restaurante.builder()
                .id(1L).nome("Cantina da Maria").tipoCozinha("Italiana")
                .dono(DonoRestaurante.builder().id(1L).nome("Maria").build())
                .build();

        command = new AdicionarItemCardapioUseCase.Command(
                "Pizza Margherita", "Clássica pizza italiana", new BigDecimal("35.90"),
                true, "/fotos/pizza.jpg", 1L
        );
    }

    @Test
    void deveAdicionarItemComSucesso() {
        var itemSalvo = ItemCardapio.builder()
                .id(1L).nome("Pizza Margherita").descricao("Clássica pizza italiana")
                .preco(new BigDecimal("35.90")).disponivel(true)
                .fotoPath("/fotos/pizza.jpg").restaurante(restaurante)
                .build();

        when(restauranteGateway.buscarPorId(1L)).thenReturn(Optional.of(restaurante));
        when(gateway.salvar(any())).thenReturn(itemSalvo);

        var resultado = useCase.execute(command);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Pizza Margherita");
        assertThat(resultado.getPreco()).isEqualByComparingTo("35.90");
        assertThat(resultado.isDisponivel()).isTrue();
        assertThat(resultado.getRestaurante().getId()).isEqualTo(1L);
        verify(gateway).salvar(any());
    }

    @Test
    void deveLancarExcecaoSeRestauranteNaoEncontrado() {
        when(restauranteGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        var commandComRestauranteInexistente = new AdicionarItemCardapioUseCase.Command(
                "Pizza", "Desc", new BigDecimal("20.00"), true, null, 99L
        );

        assertThatThrownBy(() -> useCase.execute(commandComRestauranteInexistente))
                .isInstanceOf(RestauranteNaoEncontradoException.class);

        verify(gateway, never()).salvar(any());
    }
}
