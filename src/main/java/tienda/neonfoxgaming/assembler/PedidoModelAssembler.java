package tienda.neonfoxgaming.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import tienda.neonfoxgaming.controller.v1.PedidoController;
import tienda.neonfoxgaming.dto.PedidoDto;

@Component
public class PedidoModelAssembler implements RepresentationModelAssembler<PedidoDto, EntityModel<PedidoDto>> {
    @Override
    public EntityModel<PedidoDto> toModel(PedidoDto pedido) {
        return EntityModel.of(pedido,
                linkTo(PedidoController.class).slash(pedido.codigo()).withSelfRel(),
                linkTo(PedidoController.class).withRel("pedidos"));
    }
}
