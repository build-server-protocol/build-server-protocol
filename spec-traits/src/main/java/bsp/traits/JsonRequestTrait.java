package bsp.traits;

import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.StringTrait;

public final class JsonRequestTrait extends StringTrait {

	public static final ShapeId ID = ShapeId.from("jsonrpc#jsonRequest");

	public JsonRequestTrait(String value, SourceLocation sourceLocation) {
		super(ID, value, sourceLocation);
	}

	public JsonRequestTrait(String value) {
		this(value, SourceLocation.NONE);
	}

	public static final class Provider extends StringTrait.Provider<JsonRequestTrait> {
		public Provider() {
			super(ID, JsonRequestTrait::new);
		}
	}
}
