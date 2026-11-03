package br.com.fiap.erestaurante.infra.web.mapper;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.AdicionarItemCardapioUseCase;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.AtualizarItemCardapioUseCase;
import br.com.fiap.erestaurante.infra.web.json.ItemCardapioRequest;
import br.com.fiap.erestaurante.infra.web.json.ItemCardapioResponse;
import br.com.fiap.erestaurante.infra.web.json.ItemCardapioUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemCardapioWebMapper {

    AdicionarItemCardapioUseCase.Command toAddCommand(ItemCardapioRequest request);

    AtualizarItemCardapioUseCase.Command toUpdateCommand(ItemCardapioUpdateRequest request);

    @Mapping(source = "restaurante.id", target = "restauranteId")
    @Mapping(source = "restaurante.nome", target = "restauranteNome")
    ItemCardapioResponse toResponse(ItemCardapio item);

    List<ItemCardapioResponse> toResponseList(List<ItemCardapio> items);
}
