name := "block-signer"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {

  val cassandraVersion  = "3.1.1"

  Seq(
    "org.apache.cassandra"    % "cassandra-all"             % cassandraVersion,
    "org.slf4j"               % "slf4j-api"                 % "1.7.5",
    "org.scalatest"           % "scalatest_2.11"            % "2.2.1"               % "test",
    "com.typesafe"            % "config"                    % "1.3.1"
  )
}

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

assemblyMergeStrategy in assembly := {
  case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".RSA" => MergeStrategy.discard
  case PathList(ps @ _*) if ps.last endsWith ".keys" => MergeStrategy.discard
  case PathList(ps @ _*) if ps.last endsWith "logs" => MergeStrategy.discard

  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
