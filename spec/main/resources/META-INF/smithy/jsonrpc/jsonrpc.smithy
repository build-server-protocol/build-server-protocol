$version: "2"

namespace jsonrpc

/// the JSON-RPC protocol,
/// see https://www.jsonrpc.org/specification
@protocolDefinition(traits: [jsonRequest, jsonNotification, enumKind])
@trait(selector: "service")
structure jsonRPC {}

/// Identifies an operation that abides by request/response semantics
/// https://www.jsonrpc.org/specification#request_object
@trait(selector: "operation")
string jsonRequest

/// Identifies an operation that abides by fire-and-forget semantics
/// see https://www.jsonrpc.org/specification#notification
@trait(selector: "operation")
string jsonNotification

/// Represents whether an enumeration is open or closed.
/// If closed, only the enumerated values ought to be accepted as valid
/// If open, all string values are accepted, and the enumeration
/// represents a suggestion.
@trait(selector: ":is(enum, intEnum)")
enum enumKind {
  OPEN = "open"
  CLOSED = "closed"
}

/// Represents the fact that a union should be serialised in an untagged
/// fashion
@trait(selector: "union")
structure untaggedUnion {}
