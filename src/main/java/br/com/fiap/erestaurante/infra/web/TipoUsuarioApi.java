package br.com.fiap.erestaurante.infra.web;

import br.com.fiap.erestaurante.core.controller.TipoUsuarioController;
import br.com.fiap.erestaurante.core.usecase.tipousuario.AtualizarTipoUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.tipousuario.CriarTipoUsuarioUseCase;
import br.com.fiap.erestaurante.infra.web.json.TipoUsuarioRequest;
import br.com.fiap.erestaurante.infra.web.json.TipoUsuarioResponse;
import br.com.fiap.erestaurante.infra.web.mapper.TipoUsuarioWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Tipos de Usuário", description = "CRUD de tipos de usuário")
@RestController
@RequestMapping("/v1/tipos-usuario")
public class TipoUsuarioApi {

    private final TipoUsuarioController controller;
    private final TipoUsuarioWebMapper mapper;

    public TipoUsuarioApi(TipoUsuarioController controller, TipoUsuarioWebMapper mapper) {
        this.controller = controller;
        this.mapper = mapper;
    }

    @Operation(summary = "Criar tipo de usuário")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TipoUsuarioResponse criar(@Valid @RequestBody TipoUsuarioRequest request) {
        var command = new CriarTipoUsuarioUseCase.Command(request.getNome());
        return mapper.toResponse(controller.criar(command));
    }

    @Operation(summary = "Listar todos os tipos de usuário")
    @GetMapping
    public List<TipoUsuarioResponse> listar() {
        return mapper.toResponseList(controller.buscarTodos());
    }

    @Operation(summary = "Buscar tipo de usuário por ID")
    @GetMapping("/{id}")
    public TipoUsuarioResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(controller.buscarPorId(id));
    }

    @Operation(summary = "Atualizar tipo de usuário")
    @PutMapping("/{id}")
    public TipoUsuarioResponse atualizar(@PathVariable Long id,
                                          @Valid @RequestBody TipoUsuarioRequest request) {
        var command = new AtualizarTipoUsuarioUseCase.Command(request.getNome());
        return mapper.toResponse(controller.atualizar(id, command));
    }

    @Operation(summary = "Excluir tipo de usuário")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        controller.excluir(id);
    }
}
