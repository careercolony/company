package com.mj.users.processor.event

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{Event, responseMessage}
import com.mj.users.mongo.EventDao.{getOneEventDetails, updateEventDetails}

import scala.concurrent.ExecutionContext.Implicits.global

class UpdateEventProcessor extends Actor with MessageConfig {

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (eventnRequestDto: Event) => {
      val origin = sender()
      val result = getOneEventDetails(eventnRequestDto.memberID, eventnRequestDto.eventID).map(eventDetails =>
        eventDetails match {
          case None => origin ! responseMessage("", noRecordFound, "")
          case Some(event) =>
            updateEventDetails(eventnRequestDto,event).map(response => origin ! responseMessage(eventnRequestDto.eventID, "", updateSuccess))
        }
      )


      result.recover {
        case e: Throwable => {
          origin ! responseMessage(eventnRequestDto.eventID, e.getMessage, "")
        }
      }

    }

  }
}