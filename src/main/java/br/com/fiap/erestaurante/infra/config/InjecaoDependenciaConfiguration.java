package br.com.fiap.erestaurante.infra.config;

import br.com.fiap.erestaurante.core.controller.ItemCardapioController;
import br.com.fiap.erestaurante.core.controller.RestauranteController;
import br.com.fiap.erestaurante.core.controller.TipoUsuarioController;
import br.com.fiap.erestaurante.core.controller.UsuarioController;
import br.com.fiap.erestaurante.core.gateway.ItemCardapioGateway;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.AdicionarItemCardapioUseCase;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.AdicionarItemCardapioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.AtualizarItemCardapioUseCase;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.AtualizarItemCardapioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.BuscarItemCardapioUseCase;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.BuscarItemCardapioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.ExcluirItemCardapioUseCase;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.ExcluirItemCardapioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.restaurante.AtualizarRestauranteUseCase;
import br.com.fiap.erestaurante.core.usecase.restaurante.AtualizarRestauranteUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.restaurante.BuscarRestauranteUseCase;
import br.com.fiap.erestaurante.core.usecase.restaurante.BuscarRestauranteUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.restaurante.CriarRestauranteUseCase;
import br.com.fiap.erestaurante.core.usecase.restaurante.CriarRestauranteUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.restaurante.ExcluirRestauranteUseCase;
import br.com.fiap.erestaurante.core.usecase.restaurante.ExcluirRestauranteUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.tipousuario.AtualizarTipoUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.tipousuario.AtualizarTipoUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.tipousuario.BuscarTipoUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.tipousuario.BuscarTipoUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.tipousuario.CriarTipoUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.tipousuario.CriarTipoUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.tipousuario.ExcluirTipoUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.tipousuario.ExcluirTipoUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.usuario.AtualizarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.AtualizarUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.usuario.AutenticarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.AutenticarUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.usuario.BuscarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.BuscarUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.usuario.CadastrarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.CadastrarUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.usuario.ExcluirUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.ExcluirUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.core.usecase.usuario.TrocarSenhaUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.TrocarSenhaUseCaseImpl;
import br.com.fiap.erestaurante.infra.database.jpa.ItemCardapioJpaGateway;
import br.com.fiap.erestaurante.infra.database.jpa.RestauranteJpaGateway;
import br.com.fiap.erestaurante.infra.database.jpa.TipoUsuarioJpaGateway;
import br.com.fiap.erestaurante.infra.database.mapper.ItemCardapioPersistenceMapper;
import br.com.fiap.erestaurante.infra.database.mapper.RestaurantePersistenceMapper;
import br.com.fiap.erestaurante.infra.database.mapper.TipoUsuarioPersistenceMapper;
import br.com.fiap.erestaurante.infra.database.repository.ItemCardapioRepository;
import br.com.fiap.erestaurante.infra.database.repository.RestauranteRepository;
import br.com.fiap.erestaurante.infra.database.repository.TipoUsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Ponto único de injeção de dependências — padrão Clean Arch Intermediário.
 * Os use cases e controllers de domínio são instanciados com new aqui,
 * mantendo o core completamente livre de anotações Spring.
 */
@Configuration
public class InjecaoDependenciaConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // ── Restaurante ──────────────────────────────────────────────────────

    @Bean
    public RestauranteGateway restauranteGateway(RestauranteRepository repository,
                                                  RestaurantePersistenceMapper mapper) {
        return new RestauranteJpaGateway(repository, mapper);
    }

    @Bean
    public CriarRestauranteUseCase criarRestauranteUseCase(RestauranteGateway gateway,
                                                            UsuarioGateway usuarioGateway) {
        return new CriarRestauranteUseCaseImpl(gateway, usuarioGateway);
    }

    @Bean
    public BuscarRestauranteUseCase buscarRestauranteUseCase(RestauranteGateway gateway) {
        return new BuscarRestauranteUseCaseImpl(gateway);
    }

    @Bean
    public AtualizarRestauranteUseCase atualizarRestauranteUseCase(RestauranteGateway gateway) {
        return new AtualizarRestauranteUseCaseImpl(gateway);
    }

    @Bean
    public ExcluirRestauranteUseCase excluirRestauranteUseCase(RestauranteGateway gateway) {
        return new ExcluirRestauranteUseCaseImpl(gateway);
    }

    @Bean
    public RestauranteController restauranteController(CriarRestauranteUseCase criarUseCase,
                                                       BuscarRestauranteUseCase buscarUseCase,
                                                       AtualizarRestauranteUseCase atualizarUseCase,
                                                       ExcluirRestauranteUseCase excluirUseCase) {
        return new RestauranteController(criarUseCase, buscarUseCase, atualizarUseCase, excluirUseCase);
    }

    // ── ItemCardapio ──────────────────────────────────────────────────────

    @Bean
    public ItemCardapioGateway itemCardapioGateway(ItemCardapioRepository repository,
                                                    ItemCardapioPersistenceMapper mapper) {
        return new ItemCardapioJpaGateway(repository, mapper);
    }

    @Bean
    public AdicionarItemCardapioUseCase adicionarItemCardapioUseCase(ItemCardapioGateway itemCardapioGateway,
                                                                      RestauranteGateway restauranteGateway) {
        return new AdicionarItemCardapioUseCaseImpl(itemCardapioGateway, restauranteGateway);
    }

    @Bean
    public BuscarItemCardapioUseCase buscarItemCardapioUseCase(ItemCardapioGateway itemCardapioGateway) {
        return new BuscarItemCardapioUseCaseImpl(itemCardapioGateway);
    }

    @Bean
    public AtualizarItemCardapioUseCase atualizarItemCardapioUseCase(ItemCardapioGateway itemCardapioGateway) {
        return new AtualizarItemCardapioUseCaseImpl(itemCardapioGateway);
    }

    @Bean
    public ExcluirItemCardapioUseCase excluirItemCardapioUseCase(ItemCardapioGateway itemCardapioGateway) {
        return new ExcluirItemCardapioUseCaseImpl(itemCardapioGateway);
    }

    @Bean
    public ItemCardapioController itemCardapioController(AdicionarItemCardapioUseCase adicionarUseCase,
                                                          BuscarItemCardapioUseCase buscarUseCase,
                                                          AtualizarItemCardapioUseCase atualizarUseCase,
                                                          ExcluirItemCardapioUseCase excluirUseCase) {
        return new ItemCardapioController(adicionarUseCase, buscarUseCase, atualizarUseCase, excluirUseCase);
    }

    // ── TipoUsuario ───────────────────────────────────────────────────────

    @Bean
    public TipoUsuarioGateway tipoUsuarioGateway(TipoUsuarioRepository repository,
                                                   TipoUsuarioPersistenceMapper mapper) {
        return new TipoUsuarioJpaGateway(repository, mapper);
    }

    @Bean
    public CriarTipoUsuarioUseCase criarTipoUsuarioUseCase(TipoUsuarioGateway gateway) {
        return new CriarTipoUsuarioUseCaseImpl(gateway);
    }

    @Bean
    public BuscarTipoUsuarioUseCase buscarTipoUsuarioUseCase(TipoUsuarioGateway gateway) {
        return new BuscarTipoUsuarioUseCaseImpl(gateway);
    }

    @Bean
    public AtualizarTipoUsuarioUseCase atualizarTipoUsuarioUseCase(TipoUsuarioGateway gateway) {
        return new AtualizarTipoUsuarioUseCaseImpl(gateway);
    }

    @Bean
    public ExcluirTipoUsuarioUseCase excluirTipoUsuarioUseCase(TipoUsuarioGateway gateway) {
        return new ExcluirTipoUsuarioUseCaseImpl(gateway);
    }

    @Bean
    public TipoUsuarioController tipoUsuarioController(CriarTipoUsuarioUseCase criarUseCase,
                                                        BuscarTipoUsuarioUseCase buscarUseCase,
                                                        AtualizarTipoUsuarioUseCase atualizarUseCase,
                                                        ExcluirTipoUsuarioUseCase excluirUseCase) {
        return new TipoUsuarioController(criarUseCase, buscarUseCase, atualizarUseCase, excluirUseCase);
    }

    // ── Usuario ───────────────────────────────────────────────────────────

    @Bean
    public CadastrarUsuarioUseCase cadastrarUsuarioUseCase(UsuarioGateway gateway,
                                                            TipoUsuarioGateway tipoUsuarioGateway,
                                                            PasswordEncoder passwordEncoder) {
        return new CadastrarUsuarioUseCaseImpl(gateway, tipoUsuarioGateway, passwordEncoder);
    }

    @Bean
    public AtualizarUsuarioUseCase atualizarUsuarioUseCase(UsuarioGateway gateway) {
        return new AtualizarUsuarioUseCaseImpl(gateway);
    }

    @Bean
    public ExcluirUsuarioUseCase excluirUsuarioUseCase(UsuarioGateway gateway) {
        return new ExcluirUsuarioUseCaseImpl(gateway);
    }

    @Bean
    public BuscarUsuarioUseCase buscarUsuarioUseCase(UsuarioGateway gateway) {
        return new BuscarUsuarioUseCaseImpl(gateway);
    }

    @Bean
    public TrocarSenhaUseCase trocarSenhaUseCase(UsuarioGateway gateway,
                                                  PasswordEncoder passwordEncoder) {
        return new TrocarSenhaUseCaseImpl(gateway, passwordEncoder);
    }

    @Bean
    public AutenticarUsuarioUseCase autenticarUsuarioUseCase(UsuarioGateway gateway,
                                                              PasswordEncoder passwordEncoder) {
        return new AutenticarUsuarioUseCaseImpl(gateway, passwordEncoder);
    }

    @Bean
    public UsuarioController usuarioController(CadastrarUsuarioUseCase cadastrarUseCase,
                                               AtualizarUsuarioUseCase atualizarUseCase,
                                               ExcluirUsuarioUseCase excluirUseCase,
                                               BuscarUsuarioUseCase buscarUseCase,
                                               TrocarSenhaUseCase trocarSenhaUseCase,
                                               AutenticarUsuarioUseCase autenticarUseCase) {
        return new UsuarioController(cadastrarUseCase, atualizarUseCase, excluirUseCase,
                buscarUseCase, trocarSenhaUseCase, autenticarUseCase);
    }
}
