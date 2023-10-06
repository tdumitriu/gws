package com.tvd.generic_web_server.dal

import java.sql.DriverManager

import scala.jdk.CollectionConverters.EnumerationHasAsScala

object Util {

  def unloadDrivers(): Unit = {
    DriverManager.getDrivers.asScala.foreach { d =>
      DriverManager.deregisterDriver(d)
    }
  }
}
