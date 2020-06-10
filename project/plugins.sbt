addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.2")
addSbtPlugin("org.scalameta" % "sbt-mdoc" % "1.3.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.12")

libraryDependencies ++= Seq(
  "org.eclipse.xtend" % "org.eclipse.xtend.core" % "2.14.0",
  "org.eclipse.platform" % "org.eclipse.equinox.common" % "3.10.0",
  "org.eclipse.platform" % "org.eclipse.equinox.app" % "1.3.500"
)