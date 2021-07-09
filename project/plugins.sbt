addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.7")
addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.2.21")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.8.1")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.3")

libraryDependencies ++= Seq(
  "org.eclipse.xtend" % "org.eclipse.xtend.core" % "2.25.0",
  "org.eclipse.platform" % "org.eclipse.equinox.common" % "3.14.100",
  "org.eclipse.platform" % "org.eclipse.equinox.app" % "1.5.100"
)
