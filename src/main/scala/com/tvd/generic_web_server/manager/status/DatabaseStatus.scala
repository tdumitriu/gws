package com.tvd.generic_web_server.manager.status

import com.tvd.generic_web_server.Constant.Basic.Ok
import com.tvd.generic_web_server.Logger
import com.tvd.generic_web_server.model.SystemStatus

class DatabaseStatus extends Logger {

  def getStatus: SystemStatus = {
    logger.debug("Requesting the status of the database")

    val component = "postgres"
    val status = Ok
    val description = "600 max connections"

    SystemStatus(component, status, description)
  }
}

object DatabaseStatus {

  def apply() = new DatabaseStatus()
}
