ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "4.12.1"

lazy val root = (project in file("."))
  .settings(
    name := "gametrade"
  )
