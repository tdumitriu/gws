package com.tvd.generic_web_server.manager

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import com.tvd.generic_web_server.Constant.Basic.{Unknown, NA}
import com.tvd.generic_web_server.Constant.RoutePath.{Database, Memory}
import com.tvd.generic_web_server.Logger
import com.tvd.generic_web_server.manager.status.{DatabaseStatus, LocalSystemStatus}
import com.tvd.generic_web_server.model.SystemStatus

object HealthcheckManager extends Logger {

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def getSystemStatus(component: String): SystemStatus = {
    component match {
      case Database => DatabaseStatus().getStatus
      case Memory => LocalSystemStatus.getMemoryStatus
      case _ => SystemStatus(Unknown, Unknown, NA)
    }
  }

  def response(systemStatus: SystemStatus): HttpEntity.Strict = {
    HttpEntity(ContentTypes.`application/json`, mapper.writeValueAsString(systemStatus))
  }
}
