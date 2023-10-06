package com.tvd.generic_web_server.service

import akka.actor.Actor
import com.tvd.generic_web_server.Logger
import com.tvd.generic_web_server.manager.HealthcheckManager
import com.tvd.generic_web_server.model.SystemStatus

object ServiceHealthcheckActor {
  final case class GetSystemStatus(component: String)
}

class ServiceHealthcheckActor extends Actor with Logger {
  import ServiceHealthcheckActor._

  override def preStart(): Unit = {
    logger.debug("Starting health-check manager actor...")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    logger.error(s"Log health-check actor restarted because of: ${message.getOrElse("unknown error").toString}", reason)
  }

  def getSystemStatus(component: String): SystemStatus = {
    logger.debug("Request the system status of component [{}]", component)
    HealthcheckManager.getSystemStatus(component)
  }

  def receive: Receive = {
    case GetSystemStatus(component) =>
      sender() ! getSystemStatus(component)
  }
}