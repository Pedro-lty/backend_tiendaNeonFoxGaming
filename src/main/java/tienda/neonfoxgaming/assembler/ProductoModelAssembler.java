package tienda.neonfoxgaming.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import tienda.neonfoxgaming.controller.v1.ProductoController;
import tienda.neonfoxgaming.dto.ProductoDto;

@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<ProductoDto, EntityModel<ProductoDto>> {
    @Override
    public EntityModel<ProductoDto> toModel(ProductoDto producto) {
        return EntityModel.of(producto,
                linkTo(ProductoController.class).slash(producto.id()).withSelfRel(),
                linkTo(ProductoController.class).withRel("productos"));
    }
}
