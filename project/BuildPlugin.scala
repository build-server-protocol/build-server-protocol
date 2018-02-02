package build

import sbt.{PluginTrigger, Plugins, Def, AutoPlugin, Keys}

object BuildPlugin extends AutoPlugin {
  import sbt.plugins.JvmPlugin
  import com.typesafe.sbt.SbtPgp
  import ch.epfl.scala.sbt.release.ReleaseEarlyPlugin

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

  val projectSettings: Seq[Def.Setting[_]] = Nil
  val globalSettings: Seq[Def.Setting[_]] = Nil
  val buildSettings: Seq[Def.Setting[_]] = List(
    BuildKeys.ourDynVerInstance :=
      sbtdynver.DynVer(Some(Keys.baseDirectory.in(sbt.ThisBuild).value)),
    DynVerKeys.dynver := BuildKeys.ourDynVerInstance.value.version(new java.util.Date),
    DynVerKeys.dynverGitDescribeOutput := {
      val instance = BuildKeys.ourDynVerInstance.value
      instance.getGitDescribeOutput(DynVerKeys.dynverCurrentDate.value)
    }
  )
}
