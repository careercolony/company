package com.mj.users.processor.company

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{LocationRequest, responseMessage}
import com.mj.users.mongo.CompanyDao.updateLocation

import scala.concurrent.ExecutionContext.Implicits.global

class AddLocationProcessor extends Actor with MessageConfig{

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (locationRequestDto: LocationRequest) => {
      val origin = sender()
      val result = updateLocation(locationRequestDto).map(response =>
        origin ! responseMessage(locationRequestDto.coyID, "", updateSuccess)
      )

      result.recover {
        case e: Throwable => {
          origin ! responseMessage(locationRequestDto.coyID, e.getMessage, "")
        }
      }
    }
  }
}
