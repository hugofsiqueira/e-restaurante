package br.com.fiap.erestaurante.infra.web;

import br.com.fiap.erestaurante.core.controller.ItemCardapioController;
import br.com.fiap.erestaurante.infra.web.json.ItemCardapioRequest;
import br.com.fiap.erestaurante.infra.web.json.ItemCardapioResponse;
import br.com.fiap.erestaurante.infra.web.json.ItemCardapioUpdateRequest;
import br.com.fiap.erestaurante.infra.web.mapper.ItemCardapioWebMapper;
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

@Tag(name = "Itens de Cardápio", description = "CRUD de itens do cardápio")
@RestController
@RequestMapping("/v1/itens-cardapio")
public class ItemCardapioApi {

    private final ItemCardapioController controller;
    private final ItemCardapioWebMapper mapper;

    public ItemCardapioApi(ItemCardapioController controller, ItemCardapioWebMapper mapper) {
        this.controller = controller;
        this.mapper = mapper;
    }

    @Operation(summary = "Adicionar item ao cardápio")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemCardapioResponse adicionar(@Valid @RequestBody ItemCardapioRequest request) {
        return mapper.toResponse(controller.adicionar(mapper.toAddCommand(request)));
    }

    @Operation(summary = "Buscar itens por restaurante")
    @GetMapping
    public List<ItemCardapioResponse> buscarPorRestaurante(@RequestParam Long restauranteId) {
        return mapper.toResponseList(controller.buscarPorRestaurante(restauranteId));
    }

    @Operation(summary = "Buscar item por ID")
    @GetMapping("/{id}")
    public ItemCardapioResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(controller.buscarPorId(id));
    }

    @Operation(summary = "Atualizar item do cardápio")
    @PutMapping("/{id}")
    public ItemCardapioResponse atualizar(@PathVariable Long id,
                                          @Valid @RequestBody ItemCardapioUpdateRequest request) {
        return mapper.toResponse(controller.atualizar(id, mapper.toUpdateCommand(request)));
    }

    @Operation(summary = "Excluir item do cardápio")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        controller.excluir(id);
    }
}
