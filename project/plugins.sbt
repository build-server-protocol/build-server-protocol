addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.12")
addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.3.8")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.16")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

libraryDependencies ++= Seq(
  "org.eclipse.xtend" % "org.eclipse.xtend.core" % "2.32.0"
)
