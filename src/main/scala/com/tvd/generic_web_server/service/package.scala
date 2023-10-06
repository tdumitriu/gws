package com.tvd.generic_web_server

import akka.http.scaladsl.model.{HttpResponse, IllegalUriException, StatusCodes}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}

package object service {

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  implicit def myRejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
        case MissingCookieRejection(cookieName) =>
          complete(HttpResponse(StatusCodes.BadRequest, entity = "No cookies, no service!!!"))
      }
      .handle {
        case AuthorizationFailedRejection =>
          complete((StatusCodes.Forbidden, "You're not authorized!"))
      }
      .handle {
        case ValidationRejection(msg, _) =>
          complete((StatusCodes.InternalServerError, "That wasn't valid! " + msg))
      }
      .handleAll[MethodRejection] { methodRejections =>
        val names = methodRejections.map(_.supported.name)
        complete((StatusCodes.MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
      }
      .handleNotFound {
        complete((StatusCodes.NotFound, "Not found here!"))
      }
      .handle {
        case msg =>
          complete((StatusCodes.InternalServerError, "Not Implemented Error! " + msg))
      }
      .result()
}