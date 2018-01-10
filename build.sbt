inThisBuild(
  organization := "ch.epfl.scala"
)

val bsp = project
  .settings(
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value
    )
  )
