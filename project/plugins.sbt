addSbtPlugin("ch.epfl.scala" % "sbt-release-early" % "2.1.0")
addSbtPlugin("ch.epfl.scala" % "sbt-bloop" % "1.0.0")
addSbtCoursier

libraryDependencies ++= Seq(
  "org.eclipse.xtend" % "org.eclipse.xtend.core" % "2.14.0",
  "org.eclipse.platform" % "org.eclipse.equinox.common" % "3.10.0",
  "org.eclipse.platform" % "org.eclipse.equinox.app" % "1.3.500"
)
