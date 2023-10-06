package com.tvd.generic_web_server.service

import akka.actor.Actor
import com.tvd.generic_web_server.Logger
import com.tvd.generic_web_server.manager.LogLevelManager

object ServiceLogLevelActor {
  final case class SetLogLevel(logLevel: String, path: String)
  final case class GetLogLevel(path: String)
}

class ServiceLogLevelActor extends Actor with Logger {
  import ServiceLogLevelActor._

  override def preStart(): Unit = {
    logger.debug("Starting log manager actor...")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    logger.error(s"Log manager actor restarted because of: ${message.getOrElse("unknown error").toString}", reason)
  }

  def getLogLevel(path: String): String = {
    logger.debug("Getting the log level for path [{}]", path)
    mapper.writeValueAsString(LogLevelManager.getLogLevel(path))
  }

  def setLogLevel(logLevel: String, path: String): String = {
    logger.info("Log level changed to [{}] for path [{}]", logLevel, path)
    mapper.writeValueAsString(LogLevelManager.setLogLevel(logLevel, path))
  }

  def receive: Receive = {
    case SetLogLevel(logLevel, path) =>
      sender() ! setLogLevel(logLevel, path)
    case GetLogLevel(path) =>
      sender() ! getLogLevel(path)
  }
}