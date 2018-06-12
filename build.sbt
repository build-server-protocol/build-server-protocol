val bsp = project
  .in(file("."))
  .settings(
    publishArtifact in Test := false,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
    libraryDependencies ++= List(
      "io.circe" %% "circe-core" % "0.9.0",
      "org.scalameta" %% "lsp4s" % "0.1.0"
    )
  )
