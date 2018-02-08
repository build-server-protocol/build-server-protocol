val bsp = project
  .in(file("."))
  .settings(
    libraryDependencies ++= List(
      "io.github.scalapb-json" %% "scalapb-circe" % "0.1.1",
      "org.scalameta" %% "lsp4s" % "4d51e1ae"
    ),
    PB.targets in Compile := Seq(
      scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value
    )
  )
