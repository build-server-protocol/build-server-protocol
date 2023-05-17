package bsp.traits;

import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.StringTrait;

public final class JsonNotificationTrait extends StringTrait {

	public static final ShapeId ID = ShapeId.from("jsonrpc#jsonNotification");

	public JsonNotificationTrait(String value, SourceLocation sourceLocation) {
		super(ID, value, sourceLocation);
	}

	public JsonNotificationTrait(String value) {
		this(value, SourceLocation.NONE);
	}

	public static final class Provider extends StringTrait.Provider<JsonNotificationTrait> {
		public Provider() {
			super(ID, JsonNotificationTrait::new);
		}
	}
}
