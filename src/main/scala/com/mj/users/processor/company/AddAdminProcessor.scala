package com.mj.users.processor.company

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{Admin, responseMessage}
import com.mj.users.mongo.CompanyDao.updateAdmin

import scala.concurrent.ExecutionContext.Implicits.global

class AddAdminProcessor extends Actor with MessageConfig{

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (adminRequestDto: Admin) => {
      val origin = sender()
      val result = updateAdmin(adminRequestDto).map(response =>
        origin ! responseMessage(adminRequestDto.memberID, "", updateSuccess)
      )

      result.recover {
        case e: Throwable => {
          origin ! responseMessage(adminRequestDto.memberID, e.getMessage, "")
        }
      }
    }
  }
}
