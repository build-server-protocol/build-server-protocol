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
      "org.scalameta" %% "lsp4s" % "34f9d1e3",
    ),
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value
    )
  )
