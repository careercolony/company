package com.mj.users.processor.update

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{UpdateRequest, responseMessage}
import com.mj.users.mongo.UpdateDao.insertNewUpdate

import scala.concurrent.ExecutionContext.Implicits.global

class NewUpdateProcessor extends Actor with MessageConfig{

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (updateRequestDto: UpdateRequest) => {
      val origin = sender()
      val result = insertNewUpdate(updateRequestDto).map(response =>
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
