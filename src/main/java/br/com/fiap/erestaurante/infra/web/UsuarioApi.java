package br.com.fiap.erestaurante.infra.web;

import br.com.fiap.erestaurante.core.controller.UsuarioController;
import br.com.fiap.erestaurante.core.usecase.usuario.TrocarSenhaUseCase;
import br.com.fiap.erestaurante.infra.web.json.AtualizarUsuarioRequest;
import br.com.fiap.erestaurante.infra.web.json.CadastrarUsuarioRequest;
import br.com.fiap.erestaurante.infra.web.json.TrocarSenhaRequest;
import br.com.fiap.erestaurante.infra.web.json.UsuarioResponse;
import br.com.fiap.erestaurante.infra.web.mapper.UsuarioWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários do e-Restaurante")
public class UsuarioApi {

    private final UsuarioController usuarioController;
    private final UsuarioWebMapper mapper;

    @PostMapping
    @Operation(summary = "Cadastrar usuário", description = "Cria um novo usuário (Cliente ou Dono de Restaurante)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastrarUsuarioRequest request) {
        var usuario = usuarioController.cadastrar(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(usuario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza nome, email e endereço")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado por outro usuário"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody AtualizarUsuarioRequest request) {
        var usuario = usuarioController.atualizar(id, mapper.toCommand(request));
        return ResponseEntity.ok(mapper.toResponse(usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioController.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Buscar usuários por nome", description = "Busca parcial e case-insensitive pelo nome")
    @ApiResponse(responseCode = "200", description = "Lista de usuários encontrados (pode ser vazia)")
    public ResponseEntity<List<UsuarioResponse>> buscarPorNome(@RequestParam String nome) {
        var usuarios = usuarioController.buscarPorNome(nome);
        return ResponseEntity.ok(mapper.toResponseList(usuarios));
    }

    @PatchMapping("/{id}/senha")
    @Operation(summary = "Trocar senha", description = "Altera a senha validando a senha atual")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    public ResponseEntity<Void> trocarSenha(@PathVariable Long id,
                                             @Valid @RequestBody TrocarSenhaRequest request) {
        usuarioController.trocarSenha(id, new TrocarSenhaUseCase.Command(request.getSenhaAtual(), request.getNovaSenha()));
        return ResponseEntity.noContent().build();
    }
}
