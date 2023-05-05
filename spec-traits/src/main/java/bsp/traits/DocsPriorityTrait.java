package bsp.traits;

import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.model.traits.TraitService;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.ToSmithyBuilder;

public class DocsPriorityTrait extends AbstractTrait implements ToSmithyBuilder<DocsPriorityTrait> {

    public static final ShapeId ID = ShapeId.from("docs#docsPriority");

    private final int priority;

    private DocsPriorityTrait(Builder builder) {
        super(ID, builder.getSourceLocation());
        this.priority = builder.priority;
    }

    public int getPriority() {
        return this.priority;
    }

    @Override
    protected Node createNode() {
        return Node.from(priority);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SmithyBuilder<DocsPriorityTrait> toBuilder() {
        return builder().priority(priority).sourceLocation(getSourceLocation());
    }

    public static final class Builder extends AbstractTraitBuilder<DocsPriorityTrait, DocsPriorityTrait.Builder> {

        private int priority;

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        @Override
        public DocsPriorityTrait build() {
            return new DocsPriorityTrait(this);
        }
    }

    public static final class Provider implements TraitService {

        @Override
        public ShapeId getShapeId() {
            return ID;
        }

        @Override
        public DocsPriorityTrait createTrait(ShapeId target, Node value) {
            int priority = value.expectNumberNode().getValue().intValue();
            return builder().priority(priority).sourceLocation(value).build();
        }
    }
}
