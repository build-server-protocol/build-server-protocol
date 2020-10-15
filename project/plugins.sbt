addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.3")
addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.2.9")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.12")

libraryDependencies ++= Seq(
  "org.eclipse.xtend" % "org.eclipse.xtend.core" % "2.14.0",
  "org.eclipse.platform" % "org.eclipse.equinox.common" % "3.10.0",
  "org.eclipse.platform" % "org.eclipse.equinox.app" % "1.3.500"
)
