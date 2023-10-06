package com.tvd.generic_web_server.manager

import com.tvd.generic_web_server.Constant.Basic.Ok
import com.tvd.generic_web_server.Logger
import com.tvd.generic_web_server.manager.log.LogProcessor
import com.tvd.generic_web_server.model.{LogLevel, LogLevelChange}

object LogLevelManager extends Logger {

  def setLogLevel(logLevel: String, path: String): LogLevelChange = {
    logger.debug("Processing the 'change log level' request")
    val (originalLogLevel, newLogLevel) = LogProcessor.set(logLevel, path)
    // TODO: This sleep is added here to test a long running job.. It's going to be removed
    // Thread.sleep(20000)
    LogLevelChange(Ok, path, originalLogLevel, newLogLevel)
  }

  def getLogLevel(path: String): LogLevel = {
    logger.debug("Processing the 'get log level' request for path [{}]", path)
    val logLevel = LogProcessor.get(path)
    LogLevel(path, logLevel)
  }
}
