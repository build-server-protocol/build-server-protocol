$version: "2"

namespace bsp

list JavacOptions {
    member: String
}

list Classpath {
    member: String
}

structure JavacOptionsItem {
    @required
    target: BuildTargetIdentifier
    @required
    options: JavacOptions
    @required
    classpath: Classpath
    @required
    classDirectory: String
}

structure JavacOptionsParams {
    @required
    targets: BuildTargetIdentifiers
}

list JavacOptionsItems {
    member: JavacOptionsItem
}

structure JavacOptionsResult {
    @required
    items: JavacOptionsItems
}
