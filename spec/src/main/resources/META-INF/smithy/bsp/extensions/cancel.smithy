$version: "2"

namespace bsp.cancel

use traits#jsonNotification
use traits#jsonRPC
use traits#untaggedUnion

@jsonRPC
service CancelExtension {
    operations: [
        CancelRequest
    ]
}

/// Like the language server protocol, a notification to ask the server to cancel a request.
@jsonNotification("$/cancelRequest")
operation CancelRequest {
    input: CancelRequestParams
}

structure CancelRequestParams {
    /// The request id to cancel.
    @required
    id: RequestId
}

/// Represents the identifier of a JsonRpc request id.
@untaggedUnion
union RequestId {
    string: String
    integer: Integer
}
