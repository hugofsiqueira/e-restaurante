package br.com.fiap.erestaurante.core.usecase.itemcardapio;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.ItemCardapioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.ItemCardapioGateway;
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
class AtualizarItemCardapioUseCaseImplTest {

    @Mock
    private ItemCardapioGateway gateway;

    private AtualizarItemCardapioUseCaseImpl useCase;

    private ItemCardapio itemExistente;

    @BeforeEach
    void setUp() {
        useCase = new AtualizarItemCardapioUseCaseImpl(gateway);

        itemExistente = ItemCardapio.builder()
                .id(1L).nome("Pizza Margherita").descricao("Clássica")
                .preco(new BigDecimal("35.90")).disponivel(true)
                .restaurante(Restaurante.builder().id(1L).nome("Cantina da Maria").build())
                .build();
    }

    @Test
    void deveAtualizarItemComSucesso() {
        var command = new AtualizarItemCardapioUseCase.Command(
                "Pizza Margherita Especial", "Com molho especial",
                new BigDecimal("42.00"), false, "/fotos/pizza-especial.jpg"
        );
        var itemAtualizado = ItemCardapio.builder()
                .id(1L).nome("Pizza Margherita Especial").descricao("Com molho especial")
                .preco(new BigDecimal("42.00")).disponivel(false)
                .fotoPath("/fotos/pizza-especial.jpg")
                .restaurante(itemExistente.getRestaurante())
                .build();

        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(itemExistente));
        when(gateway.salvar(any())).thenReturn(itemAtualizado);

        var resultado = useCase.execute(1L, command);

        assertThat(resultado.getNome()).isEqualTo("Pizza Margherita Especial");
        assertThat(resultado.getPreco()).isEqualByComparingTo("42.00");
        assertThat(resultado.isDisponivel()).isFalse();
        verify(gateway).salvar(any());
    }

    @Test
    void deveLancarExcecaoSeItemNaoEncontrado() {
        var command = new AtualizarItemCardapioUseCase.Command(
                "Qualquer", "Desc", new BigDecimal("10.00"), true, null
        );

        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, command))
                .isInstanceOf(ItemCardapioNaoEncontradoException.class);

        verify(gateway, never()).salvar(any());
    }
}
