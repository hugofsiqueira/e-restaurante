package br.com.fiap.erestaurante.infra.web.mapper;

import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.usecase.usuario.AtualizarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.CadastrarUsuarioUseCase;
import br.com.fiap.erestaurante.infra.web.json.AtualizarUsuarioRequest;
import br.com.fiap.erestaurante.infra.web.json.CadastrarUsuarioRequest;
import br.com.fiap.erestaurante.infra.web.json.EnderecoResponse;
import br.com.fiap.erestaurante.infra.web.json.LoginResponse;
import br.com.fiap.erestaurante.infra.web.json.TipoUsuarioResponse;
import br.com.fiap.erestaurante.infra.web.json.UsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioWebMapper {

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

    UsuarioResponse toResponse(Usuario usuario);

    LoginResponse toLoginResponse(Usuario usuario);

    TipoUsuarioResponse toResponse(TipoUsuario tipoUsuario);

    EnderecoResponse toResponse(Endereco endereco);

    List<UsuarioResponse> toResponseList(List<Usuario> usuarios);
}
