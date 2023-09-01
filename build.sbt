val pdp11 = project
  .in(file("."))
  .settings(
    name := "pdp11",
    scalaVersion := "3.3.0",
    fork := true,
    scalacOptions ++= Seq(
      "-Wunused:all",
      "-rewrite",
      "-explain",
      "-deprecation",
      "-Ykind-projector",
      "-java-output-version", "17",
    ),
    javaOptions ++= Seq(
      "-XX:+UnlockDiagnosticVMOptions",
      "-XX:+DebugNonSafepoints",
      // "-Xmx200m",
      // "-XX:+UseSerialGC"
      // "-XX:+PrintInlining",
      // "-XX:MaxInlineLevel=30"
    ),
    libraryDependencies += "co.fs2" %% "fs2-io" % "3.8.0",
    // libraryDependencies += "org.typelevel" %% "cats-laws" % "2.10.0" % Test,
    libraryDependencies += "org.typelevel" %% "discipline-munit" % "2.0.0-M3" % Test,
  )
