package com.mj.users.processor.event

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{EventRequest, responseMessage}
import com.mj.users.mongo.EventDao.insertNewEvent

import scala.concurrent.ExecutionContext.Implicits.global

class NewEventProcessor extends Actor with MessageConfig{

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (eventRequestDto: EventRequest) => {
      val origin = sender()
      val result = insertNewEvent(eventRequestDto).map(response =>
        origin ! response
      )

      result.recover {
        case e: Throwable => {
          origin ! responseMessage("", e.getMessage, "")
        }
      }
    }
  }
}
