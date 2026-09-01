package br.com.fiap.erestaurante.infrastructure.web.mapper;

import br.com.fiap.erestaurante.domain.model.Endereco;
import br.com.fiap.erestaurante.domain.model.Usuario;
import br.com.fiap.erestaurante.domain.port.in.AtualizarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.in.CadastrarUsuarioUseCase;
import br.com.fiap.erestaurante.infrastructure.web.dto.request.AtualizarUsuarioRequest;
import br.com.fiap.erestaurante.infrastructure.web.dto.request.CadastrarUsuarioRequest;
import br.com.fiap.erestaurante.infrastructure.web.dto.response.EnderecoResponse;
import br.com.fiap.erestaurante.infrastructure.web.dto.response.LoginResponse;
import br.com.fiap.erestaurante.infrastructure.web.dto.response.UsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioWebMapper {

    // ── Request → Command ─────────────────────────────────────────────

    @Mapping(source = "endereco.logradouro", target = "logradouro")
    @Mapping(source = "endereco.numero", target = "numero")
    @Mapping(source = "endereco.complemento", target = "complemento")
    @Mapping(source = "endereco.bairro", target = "bairro")
    @Mapping(source = "endereco.cidade", target = "cidade")
    @Mapping(source = "endereco.estado", target = "estado")
    @Mapping(source = "endereco.cep", target = "cep")
    CadastrarUsuarioUseCase.Command toCommand(CadastrarUsuarioRequest request);

    @Mapping(source = "endereco.logradouro", target = "logradouro")
    @Mapping(source = "endereco.numero", target = "numero")
    @Mapping(source = "endereco.complemento", target = "complemento")
    @Mapping(source = "endereco.bairro", target = "bairro")
    @Mapping(source = "endereco.cidade", target = "cidade")
    @Mapping(source = "endereco.estado", target = "estado")
    @Mapping(source = "endereco.cep", target = "cep")
    AtualizarUsuarioUseCase.Command toCommand(AtualizarUsuarioRequest request);

    // ── Domain → Response ─────────────────────────────────────────────

    @Mapping(source = "tipo", target = "tipo")
    UsuarioResponse toResponse(Usuario usuario);

    LoginResponse toLoginResponse(Usuario usuario);

    EnderecoResponse toResponse(Endereco endereco);

    List<UsuarioResponse> toResponseList(List<Usuario> usuarios);
}
