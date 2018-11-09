package build

import ch.epfl.scala.sbt.release.ReleaseEarlyPlugin
import ch.epfl.scala.sbt.release.{AutoImported => ReleaseEarlyKeys}
import sbt.{AutoPlugin, Compile, Def, Keys, PluginTrigger, Plugins, url, Developer}
import sbtdynver.GitDescribeOutput

object BuildPlugin extends AutoPlugin {
  import sbt.plugins.JvmPlugin
  import com.typesafe.sbt.SbtPgp

  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = JvmPlugin && ReleaseEarlyPlugin && SbtPgp
  val autoImport = BuildKeys

  override def globalSettings: Seq[Def.Setting[_]] =
    BuildImplementation.globalSettings
  override def buildSettings: Seq[Def.Setting[_]] =
    BuildImplementation.buildSettings
  override def projectSettings: Seq[Def.Setting[_]] =
    BuildImplementation.projectSettings
}

object BuildKeys {
  val ourDynVerInstance =
    sbt.settingKey[sbtdynver.DynVer]("Dynver that respects base dir.")
}

object BuildImplementation {
  import sbtdynver.DynVerPlugin.{autoImport => DynVerKeys}

  // This should be added to upstream sbt.
  private def GitHub(org: String, project: String): java.net.URL =
    url(s"https://github.com/$org/$project")
  private def GitHubDev(handle: String, fullName: String, email: String) =
    Developer(handle, fullName, email, url(s"https://github.com/$handle"))

  private final val ThisRepo = GitHub("scalacenter", "bloop")
  val globalSettings: Seq[Def.Setting[_]] = List(
    Keys.startYear := Some(2017),
    Keys.autoAPIMappings := true,
    Keys.publishMavenStyle := true,
    Keys.homepage := Some(ThisRepo),
    Keys.licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    Keys.developers := List(
      GitHubDev("olafurpg", "Ólafur Páll Geirsson", "olafurpg@gmail.com"),
      GitHubDev("jvican", "Jorge Vicente Cantero", "jorge@vican.me"),
      GitHubDev("jastice", "Justin Kaeser", "justin@justinkaeser.com"),
    ),
  )

  val buildSettings: Seq[Def.Setting[_]] = List(
    Keys.organization := "ch.epfl.scala",
    Keys.resolvers ++= List(
      sbt.Resolver.bintrayRepo("scalameta", "maven"),
      sbt.Resolver.bintrayRepo("scalacenter", "releases")
    ),
    ReleaseEarlyKeys.releaseEarlyWith := ReleaseEarlyKeys.SonatypePublisher,
    BuildKeys.ourDynVerInstance :=
      sbtdynver.DynVer(Some(Keys.baseDirectory.in(sbt.ThisBuild).value)),
    DynVerKeys.dynver := BuildKeys.ourDynVerInstance.value.version(new java.util.Date),
    DynVerKeys.dynverGitDescribeOutput := {
      val instance = BuildKeys.ourDynVerInstance.value
      instance.getGitDescribeOutput(DynVerKeys.dynverCurrentDate.value)
    }
  )

  val projectSettings: Seq[Def.Setting[_]] = List(
    Keys.publishArtifact in (Compile, Keys.packageDoc) := {
      val output = DynVerKeys.dynverGitDescribeOutput.value
      val version = Keys.version.value
      publishDocAndSourceArtifact(output, version)
    },
    Keys.publishArtifact in (Compile, Keys.packageSrc) := {
      val output = DynVerKeys.dynverGitDescribeOutput.value
      val version = Keys.version.value
      publishDocAndSourceArtifact(output, version)
    },
  )

  /**
    * This setting figures out whether the version is a snapshot or not and configures
    * the source and doc artifacts that are published by the build.
    *
    * Snapshot is a term with no clear definition. In this code, a snapshot is a revision
    * that is dirty, e.g. has time metadata in its representation. In those cases, the
    * build will not publish doc and source artifacts by any of the publishing actions.
    */
  def publishDocAndSourceArtifact(info: Option[GitDescribeOutput], version: String): Boolean = {
    val isStable = info.map(_.dirtySuffix.value.isEmpty)
    !isStable.map(stable => !stable || version.endsWith("-SNAPSHOT")).getOrElse(false)
  }
}
