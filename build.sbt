inThisBuild(
  List(
    organization := "ch.epfl.scala",
  )
)

val bsp = project
  .in(file("."))
  .settings(
    resolvers += Resolver.bintrayRepo("scalameta", "maven"),
    libraryDependencies ++= List(
      "io.github.scalapb-json" %% "scalapb-circe" % "0.1.1",
      "org.scalameta" %% "lsp4s" % "0ee0bab9"
    ),
    PB.targets in Compile := Seq(
      scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value
    )
  )
