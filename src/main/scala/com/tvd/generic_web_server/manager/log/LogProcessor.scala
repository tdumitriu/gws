package com.tvd.generic_web_server.manager.log

import ch.qos.logback.classic.Level
import com.tvd.generic_web_server.Constant.Logger.Level.{All, Debug, Error, Info, Off, Trace, Unknown, Warning}
import com.tvd.generic_web_server.Logger
import org.slf4j.LoggerFactory

case object LogProcessor extends Logger {

  def get(path: String): String = {
    val logbackHandler: ch.qos.logback.classic.Logger = LoggerFactory.getLogger(path).asInstanceOf[ch.qos.logback.classic.Logger]
    val originalLogLevel = logbackHandler.getLevel
    val logLevel = if(originalLogLevel == null) Unknown else originalLogLevel.toString
    logger.debug(s"Log level for [${path}] is [$logLevel]")

    logLevel
  }

  def set(logLevel: String, path: String): (String, String) = {
    val logbackHandler: ch.qos.logback.classic.Logger = LoggerFactory.getLogger(path).asInstanceOf[ch.qos.logback.classic.Logger]
    val originalLogLevel = logbackHandler.getLevel

    val newLogLevel = logLevel.toLowerCase match {
      case Trace   => Level.TRACE
      case Debug   => Level.DEBUG
      case Info    => Level.INFO
      case Warning => Level.WARN
      case Error   => Level.ERROR
      case All     => Level.ALL
      case Off     => Level.OFF
      case _       => logbackHandler.getLevel
    }

    val safeOriginalLogLevel = if(originalLogLevel == null) Unknown else originalLogLevel
    logger.debug(s"Request to change log level for [${path}] from [$safeOriginalLogLevel] to [$newLogLevel]")
    logbackHandler.setLevel(newLogLevel)
    logger.debug(s"Log level changed for [${path}] from [$safeOriginalLogLevel] to [$newLogLevel]")

    (safeOriginalLogLevel.toString, newLogLevel.toString)
  }
}