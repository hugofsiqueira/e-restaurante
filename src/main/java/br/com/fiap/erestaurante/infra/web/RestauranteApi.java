package br.com.fiap.erestaurante.infra.web;

import br.com.fiap.erestaurante.core.controller.RestauranteController;
import br.com.fiap.erestaurante.infra.web.json.RestauranteRequest;
import br.com.fiap.erestaurante.infra.web.json.RestauranteResponse;
import br.com.fiap.erestaurante.infra.web.mapper.RestauranteWebMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Restaurantes", description = "CRUD de restaurantes")
@RestController
@RequestMapping("/v1/restaurantes")
public class RestauranteApi {

    private final RestauranteController controller;
    private final RestauranteWebMapper mapper;

    public RestauranteApi(RestauranteController controller, RestauranteWebMapper mapper) {
        this.controller = controller;
        this.mapper = mapper;
    }

    @Operation(summary = "Criar restaurante")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestauranteResponse criar(@Valid @RequestBody RestauranteRequest request) {
        return mapper.toResponse(controller.criar(mapper.toCreateCommand(request)));
    }

    @Operation(summary = "Buscar restaurantes por nome")
    @GetMapping
    public List<RestauranteResponse> buscarPorNome(@RequestParam(required = false, defaultValue = "") String nome) {
        return mapper.toResponseList(controller.buscarPorNome(nome));
    }

    @Operation(summary = "Buscar restaurante por ID")
    @GetMapping("/{id}")
    public RestauranteResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(controller.buscarPorId(id));
    }

    @Operation(summary = "Atualizar restaurante")
    @PutMapping("/{id}")
    public RestauranteResponse atualizar(@PathVariable Long id,
                                          @Valid @RequestBody RestauranteRequest request) {
        return mapper.toResponse(controller.atualizar(id, mapper.toUpdateCommand(request)));
    }

    @Operation(summary = "Excluir restaurante")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        controller.excluir(id);
    }
}
