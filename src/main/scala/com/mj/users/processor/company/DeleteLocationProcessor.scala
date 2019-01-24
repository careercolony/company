package com.mj.users.processor.company

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{Location, responseMessage}
import com.mj.users.mongo.CompanyDao.deleteLocation

import scala.concurrent.ExecutionContext.Implicits.global

class DeleteLocationProcessor extends Actor with MessageConfig{

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (coyID: String, address: String) => {
      val origin = sender()
      val result = deleteLocation(coyID , address).map(response =>
        origin ! responseMessage(coyID, "", updateSuccess)
      )

      result.recover {
        case e: Throwable => {
          origin ! responseMessage(coyID, e.getMessage, "")
        }
      }
    }
  }
}
