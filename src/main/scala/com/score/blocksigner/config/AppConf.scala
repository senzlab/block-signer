package com.score.blocksigner.config

import com.typesafe.config.ConfigFactory

import scala.util.Try

/**
  * Load configurations define in application.conf from here
  *
  * @author eranga herath(erangaeb@gmail.com)
  */
trait AppConf {
  // config object
  val appConf = ConfigFactory.load()

  // senzie config
  lazy val keyspace = Try(appConf.getString("signer.keyspace")).getOrElse("test")
  lazy val table = Try(appConf.getString("signer.table")).getOrElse("audit")
}
