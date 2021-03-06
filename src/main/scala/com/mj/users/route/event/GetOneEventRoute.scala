package com.mj.users.route.event

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes}
import akka.http.scaladsl.server.Directives.{complete, path, _}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.mj.users.model.JsonRepo._
import com.mj.users.model.{responseMessage, _}
import org.slf4j.LoggerFactory
import spray.json._

import scala.util.{Failure, Success}

trait GetOneEventRoute {
  val GetOneEventUserLog = LoggerFactory.getLogger(this.getClass.getName)


  def GetOneEvent(system: ActorSystem): Route = {

    val GetOneEventProcessor = system.actorSelection("/*/getOneEventProcessor")
    implicit val timeout = Timeout(20, TimeUnit.SECONDS)


    path("get-one-event" / "memberID" / Segment / "eventID" / Segment) { (memberID: String, eventID: String) =>
      get {
        val userResponse = GetOneEventProcessor ? (memberID, eventID)
        onComplete(userResponse) {
          case Success(resp) =>
            resp match {
              case s: Event => {
                complete(HttpResponse(entity = HttpEntity(MediaTypes.`application/json`, s.toJson.toString)))
              }
              case s: responseMessage =>
                complete(HttpResponse(status = BadRequest, entity = HttpEntity(MediaTypes.`application/json`, s.toJson.toString)))
              case _ => complete(HttpResponse(status = BadRequest, entity = HttpEntity(MediaTypes.`application/json`, responseMessage("", resp.toString, "").toJson.toString)))
            }
          case Failure(error) =>
            GetOneEventUserLog.error("Error is: " + error.getMessage)
            complete(HttpResponse(status = BadRequest, entity = HttpEntity(MediaTypes.`application/json`, responseMessage("", error.getMessage, "").toJson.toString)))
        }


      }
    }

  }
}
