package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Cliente;
import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.exception.EmailJaCadastradoException;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarUsuarioUseCaseImplTest {

    @Mock
    private UsuarioGateway gateway;

    @Mock
    private TipoUsuarioGateway tipoUsuarioGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CadastrarUsuarioUseCaseImpl useCase;

    private TipoUsuario tipoCliente;
    private TipoUsuario tipoDono;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        useCase = new CadastrarUsuarioUseCaseImpl(gateway, tipoUsuarioGateway, passwordEncoder);
        tipoCliente = TipoUsuario.builder().id(1L).nome("CLIENTE").build();
        tipoDono = TipoUsuario.builder().id(2L).nome("DONO_RESTAURANTE").build();
        endereco = Endereco.builder()
                .logradouro("Rua das Flores").numero("123")
                .bairro("Centro").cidade("São Paulo").estado("SP").cep("01310-100")
                .build();
    }

    @Test
    void deveCadastrarClienteComSucesso() {
        var command = new CadastrarUsuarioUseCase.Command(
                "João Silva", "joao@email.com", "joaosilva", "senha123",
                1L,
                "Rua das Flores", "123", null, "Centro", "São Paulo", "SP", "01310-100"
        );
        var clienteSalvo = Cliente.builder()
                .id(1L).nome("João Silva").email("joao@email.com")
                .login("joaosilva").senha("hashSenha")
                .endereco(endereco).tipoUsuario(tipoCliente)
                .build();

        when(gateway.existePorEmail("joao@email.com")).thenReturn(false);
        when(tipoUsuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(tipoCliente));
        when(passwordEncoder.encode("senha123")).thenReturn("hashSenha");
        when(gateway.salvar(any(Usuario.class))).thenReturn(clienteSalvo);

        Usuario resultado = useCase.execute(command);

        assertThat(resultado).isInstanceOf(Cliente.class);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
        assertThat(resultado.getTipoUsuario().getNome()).isEqualTo("CLIENTE");
        verify(passwordEncoder).encode("senha123");
        verify(gateway).salvar(any(Usuario.class));
    }

    @Test
    void deveCadastrarDonoRestauranteComSucesso() {
        var command = new CadastrarUsuarioUseCase.Command(
                "Maria Souza", "maria@email.com", "mariarestaurante", "senha456",
                2L,
                "Av. Paulista", "1000", "Apto 5", "Bela Vista", "São Paulo", "SP", "01311-000"
        );
        var donoSalvo = DonoRestaurante.builder()
                .id(2L).nome("Maria Souza").email("maria@email.com")
                .login("mariarestaurante").senha("hashSenha456")
                .endereco(endereco).tipoUsuario(tipoDono)
                .build();

        when(gateway.existePorEmail("maria@email.com")).thenReturn(false);
        when(tipoUsuarioGateway.buscarPorId(2L)).thenReturn(Optional.of(tipoDono));
        when(passwordEncoder.encode("senha456")).thenReturn("hashSenha456");
        when(gateway.salvar(any(Usuario.class))).thenReturn(donoSalvo);

        Usuario resultado = useCase.execute(command);

        assertThat(resultado).isInstanceOf(DonoRestaurante.class);
        assertThat(resultado.getTipoUsuario().getNome()).isEqualTo("DONO_RESTAURANTE");
    }

    @Test
    void deveLancarEmailJaCadastradoExceptionSeEmailJaExiste() {
        var command = new CadastrarUsuarioUseCase.Command(
                "Duplicado", "existente@email.com", "login", "senha123",
                1L,
                "Rua A", "1", null, "Bairro", "Cidade", "SP", "01310-100"
        );

        when(gateway.existePorEmail("existente@email.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(EmailJaCadastradoException.class)
                .hasMessageContaining("existente@email.com");

        verify(gateway, never()).salvar(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void deveLancarTipoUsuarioNaoEncontradoExceptionSeTipoInexistente() {
        var command = new CadastrarUsuarioUseCase.Command(
                "Teste", "teste@email.com", "teste", "senha123",
                99L,
                "Rua B", "2", null, "Bairro", "Cidade", "SP", "01310-100"
        );

        when(gateway.existePorEmail("teste@email.com")).thenReturn(false);
        when(tipoUsuarioGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(TipoUsuarioNaoEncontradoException.class);

        verify(gateway, never()).salvar(any());
    }

    @Test
    void deveCriptografarSenhaAntesDeSalvar() {
        var command = new CadastrarUsuarioUseCase.Command(
                "Teste", "teste@email.com", "teste", "senhaPlana",
                1L,
                "Rua B", "2", null, "Bairro", "Cidade", "SP", "01310-100"
        );
        var salvo = Cliente.builder()
                .id(1L).nome("Teste").email("teste@email.com")
                .login("teste").senha("BCrypt$hash")
                .endereco(endereco).tipoUsuario(tipoCliente)
                .build();

        when(gateway.existePorEmail(anyString())).thenReturn(false);
        when(tipoUsuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(tipoCliente));
        when(passwordEncoder.encode("senhaPlana")).thenReturn("BCrypt$hash");
        when(gateway.salvar(any())).thenReturn(salvo);

        useCase.execute(command);

        verify(passwordEncoder).encode("senhaPlana");
    }
}
