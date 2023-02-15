package ch.epfl.smithy.jsonrpc.traits;

import software.amazon.smithy.model.SourceException;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.model.traits.TraitService;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.ToSmithyBuilder;
import java.util.Optional;

public final class DataTrait extends AbstractTrait implements ToSmithyBuilder<DataTrait> {

	public static final ShapeId ID = ShapeId.from("jsonrpc#data");

	private final String kind;
	private final ShapeId polymorphicData;

	private DataTrait(DataTrait.Builder builder) {
		super(ID, builder.getSourceLocation());
		this.kind = builder.kind;
		this.polymorphicData = builder.polymorphicData;
	}

	public ShapeId getPolymorphicData() {
		return this.polymorphicData;
	}

	public String getKind() {
		return this.kind;
	}

	@Override
	protected Node createNode() {
		ObjectNode.Builder builder = Node.objectNodeBuilder();
		builder.withMember("extends", getPolymorphicData().toString());
		builder.withMember("kind", getKind());
		return builder.build();
	}

	@Override
	public SmithyBuilder<DataTrait> toBuilder() {
		return builder().polymorphicData(polymorphicData).kind(kind).sourceLocation(getSourceLocation());
	}

	/**
	 * @return Returns a new RefinedTrait builder.
	 */
	public static DataTrait.Builder builder() {
		return new Builder();
	}

	public static final class Builder extends AbstractTraitBuilder<DataTrait, DataTrait.Builder> {

		private String kind;
		private ShapeId polymorphicData;

		public DataTrait.Builder kind(String kind) {
			this.kind = kind;
			return this;
		}

		public DataTrait.Builder polymorphicData(ShapeId polymorphicData) {
			this.polymorphicData = polymorphicData;
			return this;
		}

		@Override
		public DataTrait build() {
			return new DataTrait(this);
		}
	}

	public static final class Provider implements TraitService {

		@Override
		public ShapeId getShapeId() {
			return ID;
		}

		@Override
		public DataTrait createTrait(ShapeId target, Node value) {
			ObjectNode objectNode = value.expectObjectNode();
			ShapeId polymorphicData = ShapeId.fromNode(objectNode.expectMember("extends").expectStringNode());
			String kind = objectNode.expectMember("kind").expectStringNode().getValue();
			return builder().kind(kind).polymorphicData(polymorphicData).build();
		}
	}
}
