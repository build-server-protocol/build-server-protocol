package bsp.traits;

import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AnnotationTrait;
import software.amazon.smithy.model.traits.AbstractTrait;

public class SetTrait extends AnnotationTrait {

    public static ShapeId ID = ShapeId.from("traits#set");

    public SetTrait(ObjectNode node) {
        super(ID, node);
    }

    public SetTrait() {
        super(ID, Node.objectNode());
    }

    public static final class Provider extends AbstractTrait.Provider {
        public Provider() {
            super(ID);
        }

        @Override
        public SetTrait createTrait(ShapeId target, Node node) {
            return new SetTrait(node.expectObjectNode());
        }
    }
}
