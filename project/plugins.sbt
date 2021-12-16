addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")
addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.2.24")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.6")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.5")

libraryDependencies ++= Seq(
  "org.eclipse.xtend" % "org.eclipse.xtend.core" % "2.25.0"
)
