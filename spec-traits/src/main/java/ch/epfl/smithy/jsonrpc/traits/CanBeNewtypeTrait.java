package ch.epfl.smithy.jsonrpc.traits;

import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AnnotationTrait;

public final class CanBeNewtypeTrait extends AnnotationTrait {
    public static ShapeId ID = ShapeId.from("jsonrpc#canBeNewtype");

    public CanBeNewtypeTrait() {
        super(ID, Node.objectNode());
    }

    public static final class Provider extends AnnotationTrait.Provider<UntaggedUnionTrait> {
        public Provider() {
            super(ID, (node) -> new UntaggedUnionTrait());
        }
    }
}
