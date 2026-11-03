package br.com.fiap.erestaurante.infra.web;

import br.com.fiap.erestaurante.core.controller.UsuarioController;
import br.com.fiap.erestaurante.core.usecase.usuario.AutenticarUsuarioUseCase;
import br.com.fiap.erestaurante.infra.web.json.LoginRequest;
import br.com.fiap.erestaurante.infra.web.json.LoginResponse;
import br.com.fiap.erestaurante.infra.web.mapper.UsuarioWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/login")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login de usuários")
public class LoginApi {

    private final UsuarioController usuarioController;
    private final UsuarioWebMapper mapper;

    @PostMapping
    @Operation(summary = "Login", description = "Autentica o usuário com login e senha")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha incorreta"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var usuario = usuarioController.autenticar(
                new AutenticarUsuarioUseCase.Command(request.getLogin(), request.getSenha()));
        return ResponseEntity.ok(mapper.toLoginResponse(usuario));
    }
}
