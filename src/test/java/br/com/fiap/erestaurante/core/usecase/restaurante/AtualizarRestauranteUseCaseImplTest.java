package br.com.fiap.erestaurante.core.usecase.restaurante;

import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.HorarioFuncionamento;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.RestauranteNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarRestauranteUseCaseImplTest {

    @Mock
    private RestauranteGateway gateway;

    private AtualizarRestauranteUseCaseImpl useCase;

    private Restaurante restauranteExistente;

    @BeforeEach
    void setUp() {
        useCase = new AtualizarRestauranteUseCaseImpl(gateway);

        restauranteExistente = Restaurante.builder()
                .id(1L).nome("Cantina Velha").tipoCozinha("Italiana")
                .dono(DonoRestaurante.builder().id(1L).nome("Maria").build())
                .endereco(Endereco.builder().logradouro("Rua Antiga").numero("1")
                        .bairro("Bairro").cidade("Cidade").estado("SP").cep("01000-000").build())
                .horarioFuncionamento(HorarioFuncionamento.builder()
                        .abertura(LocalTime.of(10, 0)).fechamento(LocalTime.of(20, 0)).build())
                .build();
    }

    @Test
    void deveAtualizarRestauranteComSucesso() {
        var command = new AtualizarRestauranteUseCase.Command(
                "Cantina Nova", "Francesa",
                "Rua Nova", "99", "Sala 2", "Bairro Novo", "Rio", "RJ", "20000-000",
                LocalTime.of(12, 0), LocalTime.of(23, 0)
        );
        var atualizado = Restaurante.builder()
                .id(1L).nome("Cantina Nova").tipoCozinha("Francesa")
                .dono(DonoRestaurante.builder().id(1L).nome("Maria").build())
                .build();

        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(restauranteExistente));
        when(gateway.salvar(any())).thenReturn(atualizado);

        var resultado = useCase.execute(1L, command);

        assertThat(resultado.getNome()).isEqualTo("Cantina Nova");
        assertThat(resultado.getTipoCozinha()).isEqualTo("Francesa");
    }

    @Test
    void deveLancarExcecaoSeRestauranteNaoEncontrado() {
        var command = new AtualizarRestauranteUseCase.Command(
                "Qualquer", "Italiana",
                "Rua", "1", null, "Bairro", "Cidade", "SP", "00000-000",
                LocalTime.of(10, 0), LocalTime.of(22, 0)
        );

        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, command))
                .isInstanceOf(RestauranteNaoEncontradoException.class);

        verify(gateway, never()).salvar(any());
    }
}
