package bsp.traits;

import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AnnotationTrait;
import software.amazon.smithy.model.traits.AbstractTrait;

public class JsonRPCTrait extends AnnotationTrait {

	public static ShapeId ID = ShapeId.from("jsonrpc#jsonRPC");

	public JsonRPCTrait(ObjectNode node) {
		super(ID, node);
	}

	public JsonRPCTrait() {
		super(ID, Node.objectNode());
	}

	public static final class Provider extends AbstractTrait.Provider {
		public Provider() {
			super(ID);
		}

		@Override
		public JsonRPCTrait createTrait(ShapeId target, Node node) {
			return new JsonRPCTrait(node.expectObjectNode());
		}
	}
}
