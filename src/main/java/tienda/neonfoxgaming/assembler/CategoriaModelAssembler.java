package tienda.neonfoxgaming.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import tienda.neonfoxgaming.controller.v1.CategoriaController;
import tienda.neonfoxgaming.dto.CategoriaDto;

@Component
public class CategoriaModelAssembler implements RepresentationModelAssembler<CategoriaDto, EntityModel<CategoriaDto>> {
    @Override
    public EntityModel<CategoriaDto> toModel(CategoriaDto categoria) {
        return EntityModel.of(categoria,
                linkTo(CategoriaController.class).slash(categoria.id()).withSelfRel(),
                linkTo(CategoriaController.class).withRel("categorias"));
    }
}
