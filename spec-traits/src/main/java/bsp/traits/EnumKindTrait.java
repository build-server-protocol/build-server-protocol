package bsp.traits;

import software.amazon.smithy.model.SourceException;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.model.traits.TraitService;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.ToSmithyBuilder;

public class EnumKindTrait extends AbstractTrait implements ToSmithyBuilder<EnumKindTrait> {

	public static enum EnumKind {
		OPEN("open"), CLOSED("closed");

		private String value;

		EnumKind(String value) {
			this.value = value;
		}
	}

	public static final ShapeId ID = ShapeId.from("jsonrpc#enumKind");

	private final EnumKind enumKind;

	private EnumKindTrait(Builder builder) {
		super(ID, builder.getSourceLocation());
		this.enumKind = builder.enumKind;
		if (enumKind == null) {
			throw new SourceException("An enumKind must be provided", getSourceLocation());
		}
	}

	public EnumKind getEnumKind() {
		return this.enumKind;
	}

	@Override
	protected Node createNode() {
		return Node.from(getEnumKind().value);
	}

	@Override
	public SmithyBuilder<EnumKindTrait> toBuilder() {
		return builder().enumKind(enumKind).sourceLocation(getSourceLocation());
	}

	/**
	 * @return Returns a new EnumKindTrait builder.
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder extends AbstractTraitBuilder<EnumKindTrait, Builder> {

		private EnumKind enumKind;

		public Builder enumKind(EnumKind enumKind) {
			this.enumKind = enumKind;
			return this;
		}

		@Override
		public EnumKindTrait build() {
			return new EnumKindTrait(this);
		}
	}

	public static final class Provider implements TraitService {

		@Override
		public ShapeId getShapeId() {
			return ID;
		}

		@Override
		public EnumKindTrait createTrait(ShapeId target, Node value) {
			EnumKind enumKind = EnumKind.valueOf(value.expectStringNode().getValue().toUpperCase());
			Builder builder = new Builder().enumKind(enumKind);
			return new EnumKindTrait(builder);
		}
	}
}
