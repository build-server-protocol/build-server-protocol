inThisBuild(List(
  scalaVersion := "2.12.7",
  bloopExportJarClassifiers in Global := Some(Set("sources")),
  libraryDependencies ++= List(
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )
))

lazy val a = project
  .settings(
    scalacOptions ++= List(
      "-Yrangepos",
      "-Ywarn-unused"
    ),
    libraryDependencies ++= List(
      "com.lihaoyi" %% "ujson" % "0.6.6",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    ),
    addCompilerPlugin("org.scalameta" % "semanticdb-scalac" % "4.0.0" cross CrossVersion.full)
  )

lazy val b = project.dependsOn(a)
