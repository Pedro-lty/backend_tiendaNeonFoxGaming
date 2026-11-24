package tienda.neonfoxgaming.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import tienda.neonfoxgaming.controller.v1.UsuarioController;
import tienda.neonfoxgaming.dto.UsuarioDto;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<UsuarioDto, EntityModel<UsuarioDto>> {
    @Override
    public EntityModel<UsuarioDto> toModel(UsuarioDto usuario) {
        return EntityModel.of(usuario,
                linkTo(UsuarioController.class).slash(usuario.id()).withSelfRel(),
                linkTo(UsuarioController.class).withRel("usuarios"));
    }
}
