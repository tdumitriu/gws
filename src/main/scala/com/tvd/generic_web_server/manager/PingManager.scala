package com.tvd.generic_web_server.manager

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import com.tvd.generic_web_server.Logger

object PingManager extends Logger {

  def response(): HttpEntity.Strict = {
    logger.debug("Processing the 'ping' request")
    HttpEntity(ContentTypes.`text/plain(UTF-8)`, "pong")
  }
}
