package com.tvd.generic_web_server

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpConnectionContext}
import akka.http.scaladsl.server.Route
import com.tvd.generic_web_server.Constant.ActorSystem.Name
import com.tvd.generic_web_server.Constant.Basic.PathPrefix
import com.tvd.generic_web_server.Constant.Http._
import com.tvd.generic_web_server.security.HttpsConnection
import com.tvd.generic_web_server.service.ServiceRoutes
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}

object WebServer extends ServiceRoutes with Logger with HttpsConnection {

  override implicit def system: ActorSystem = ActorSystem(Name)
  val ProtocolHttps: String = "https"

  def main(args: Array[String]): Unit = {
    logger.info("")
    logger.info("")
    logger.info("------------------------ start ----------------------")
    logger.info("")
    logger.info("")
    logger.info("""  _____         _________""")
    logger.info("""    ______ ________ ___(_)_______ ______  /____  __""")
    logger.info("""  _  __ `/__  __ \__  / __  __ \_  __  / _  / / /""")
    logger.info("""  / /_/ / _  / / /_  /  _  / / // /_/ /  / /_/ /""")
    logger.info("""  \__,_/  /_/ /_/ /_/   /_/ /_/ \__,_/   \__,_/""")
    logger.info("")
    logger.info("")
    logger.info("------------------------------------------------------")
    logger.info("|                generic_web_server                  |")
    logger.info("------------------------------------------------------")
    logger.info("Starting the system...")

    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    lazy val routes: Route = serviceRoutes
    val conf: Config = ConfigFactory.load()

    val scheme = conf.getString(Scheme)
    val interface = conf.getString(Interface)
    val port = conf.getInt(Port)
    val isHttps = scheme.equalsIgnoreCase(ProtocolHttps)

    if(isHttps) {
//      Http().setDefaultServerHttpContext(https)
//      val bindingFuture = Http().bindAndHandle(routes, interface, port, connectionContext = https)

      val binding: Future[Http.ServerBinding] =
        Http().newServerAt("127.0.0.1", port).enableHttps(https).bind(routes)

      logger.info("The system has been started")
      logger.info(s"Server online at $scheme://localhost:$port/$PathPrefix")
      logger.info("The system is ready")
      logger.info("...")
      StdIn.readLine() // let it run until user presses return
      logger.info("System terminated")
      logger.info("")
      logger.info("------------------------- end -----------------------")
      logger.info("")

      system.registerOnTermination(println("Shutting down Actor System."))

      val onceAllConnectionsTerminated: Future[Http.HttpTerminated] =
        Await.result(binding, 10.seconds)
          .terminate(hardDeadline = 3.seconds)

      onceAllConnectionsTerminated.flatMap { _ =>
        system.terminate()
      }

//      bindingFuture
//        .flatMap(_.unbind())
//        .onComplete(_ => {
//          system.terminate()
//        })
//    } else {
//      Http().bindAndHandleAsync(
//        Route.asyncHandler(routes),
//        interface,
//        port,
//        connectionContext = HttpConnectionContext()) andThen {
//        case Success(sb) =>
//          logger.info("Server online at : {}", sb.localAddress.getAddress.toString)
//        case Failure(t) =>
//          logger.error(s"Failed to bind to {$interface}:{$port}—shutting down", t)
//          system.terminate()
//      }
    }
  }
}
