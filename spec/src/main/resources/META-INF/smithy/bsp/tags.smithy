$version: "2"

namespace bsp

use traits#docsPriority

// Applying the tags a-posteriori to avoid hurting the readability of the spec

apply Integer @tags(["basic"])
apply Integer @docsPriority(11)

apply Long @tags(["basic"])
apply Long @docsPriority(11)

apply BuildTarget @tags(["basic"])
apply BuildTarget @docsPriority(10)

apply BuildTargetIdentifier @tags(["basic"])
apply BuildTargetIdentifier @docsPriority(9)

apply TaskId @tags(["basic"])
apply TaskId @docsPriority(8)

apply StatusCode @tags(["basic"])
apply StatusCode @docsPriority(7)

apply BuildTargetCapabilities @tags(["basic"])

apply BuildTargetTag @tags(["basic"])
