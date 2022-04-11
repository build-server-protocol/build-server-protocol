addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")
addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.3.2")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.9")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")

libraryDependencies ++= Seq(
  "org.eclipse.xtend" % "org.eclipse.xtend.core" % "2.26.0"
)

// It seems that org.eclipse.extend.core:2.25.0 dependency is now resolved
// to org.eclipse.core.runtime:3.19.0 and org.eclipse.equinox.common:3.15.100.
//
// Both libraries define classes in package "org.eclipse.core.runtime". JDK requires
// all classes in a single package to be signed with the same set of certificates
// https://github.com/openjdk/jdk/blob/517967284cf607c0137e088a33ab5eb98d59542d/src/java.base/share/classes/java/lang/ClassLoader.java#L903
//
// It seems that the certifates set in Eclipse libs has changed recently, but due to a mistake in xtend's
// dependency rules, org.eclipse.core.runtime is resolved to an old version, while org.eclipse.equinox.common
// is resolved to a new one.
//
// If org.eclipse.core.runtime is bumped to a new version, then the certificates are the same.
dependencyOverrides ++= Seq(
  "org.eclipse.platform" % "org.eclipse.core.runtime" % "3.24.0"
)
