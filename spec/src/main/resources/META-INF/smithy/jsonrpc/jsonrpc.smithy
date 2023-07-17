$version: "2"

namespace jsonrpc

/// the JSON-RPC protocol,
/// see https:/// www.jsonrpc.org/specification
@protocolDefinition(traits: [
    jsonRequest
    jsonNotification
    enumKind
                    ])

@trait(selector: "service")
structure jsonRPC {
}

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

/// Trait used to mark the "base type" of a polymorphic data type
@trait()
structure data {
}

/// Represents the fact that a union should be serialised in an untagged
/// fashion
@trait(selector: "union")
structure untaggedUnion {
}

@idRef(failWhenMissing: true, selector: "document")
string PolymorphicData

/// A trait indicating that the data is meant to fulfill
/// a polymorphic "data" field. This trait is used to extend
/// the core BSP semantics with language/usecase specific
/// information
///
/// When an instance of the annotated piece of data is fulfilling a `data`
/// field, the serialised form of the object holding the `data` field should
/// also present a `dataKind` field with the value indicated by the `kind`.
@trait()
structure dataKind {
    /// This indicates the value of the `dataKind` field
    /// should take when the shape with the `@data` trait
    /// is used to fulfill a polymorphic `data` field.
    @required
    kind: String

    /// Indicates what polymorphic document type this piece
    /// of data can fulfill.
    @required
    extends: PolymorphicDataList
}

list PolymorphicDataList {
    member: PolymorphicData
}
