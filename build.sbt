inThisBuild(
  organization := "ch.epfl.scala"
)

val bsp = project
  .in(file("."))
  .settings(
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value
    )
  )
