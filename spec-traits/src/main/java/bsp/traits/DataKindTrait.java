package bsp.traits;

import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.model.traits.TraitService;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.ToSmithyBuilder;

public final class DataKindTrait extends AbstractTrait implements ToSmithyBuilder<DataKindTrait> {

	public static final ShapeId ID = ShapeId.from("jsonrpc#dataKind");

	private final String kind;
	private final ShapeId polymorphicData;

	private DataKindTrait(DataKindTrait.Builder builder) {
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
	public SmithyBuilder<DataKindTrait> toBuilder() {
		return builder().polymorphicData(polymorphicData).kind(kind).sourceLocation(getSourceLocation());
	}

	/**
	 * @return Returns a new RefinedTrait builder.
	 */
	public static DataKindTrait.Builder builder() {
		return new Builder();
	}

	public static final class Builder extends AbstractTraitBuilder<DataKindTrait, DataKindTrait.Builder> {

		private String kind;
		private ShapeId polymorphicData;

		public DataKindTrait.Builder kind(String kind) {
			this.kind = kind;
			return this;
		}

		public DataKindTrait.Builder polymorphicData(ShapeId polymorphicData) {
			this.polymorphicData = polymorphicData;
			return this;
		}

		@Override
		public DataKindTrait build() {
			return new DataKindTrait(this);
		}
	}

	public static final class Provider implements TraitService {

		@Override
		public ShapeId getShapeId() {
			return ID;
		}

		@Override
		public DataKindTrait createTrait(ShapeId target, Node value) {
			ObjectNode objectNode = value.expectObjectNode();
			ShapeId polymorphicData = ShapeId.fromNode(objectNode.expectMember("extends").expectStringNode());
			String kind = objectNode.expectMember("kind").expectStringNode().getValue();
			return builder().kind(kind).polymorphicData(polymorphicData).build();
		}
	}
}
