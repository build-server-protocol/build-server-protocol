package bsp.traits;

import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.model.traits.AnnotationTrait;

public class DataTrait extends AnnotationTrait {

    public static ShapeId ID = ShapeId.from("jsonrpc#data");

    public DataTrait(ObjectNode node) {
        super(ID, node);
    }

    public DataTrait() {
        super(ID, Node.objectNode());
    }

    public static final class Provider extends AbstractTrait.Provider {
        public Provider() {
            super(ID);
        }

        @Override
        public DataTrait createTrait(ShapeId target, Node node) {
            return new DataTrait(node.expectObjectNode());
        }
    }
}