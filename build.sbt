val bsp = project
  .in(file("."))
  .settings(
    publishArtifact in Test := false,
    sources in (Compile, doc) := Nil,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
    libraryDependencies ++= List(
      "io.circe" %% "circe-core" % "0.9.0",
      "io.circe" %% "circe-derivation" % "0.9.0-M4",
      "org.scalameta" %% "lsp4s" % "0.2.0"
    )
  )
