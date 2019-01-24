package com.mj.users.processor.company

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{Company, responseMessage}
import com.mj.users.mongo.CompanyDao.updateCompanyDetails

import scala.concurrent.ExecutionContext.Implicits.global

class UpdateCompanyProcessor extends Actor with MessageConfig {

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (companynRequestDto: Company) => {
      val origin = sender()
      val result = updateCompanyDetails(companynRequestDto)
        .map(response => origin ! responseMessage(companynRequestDto.coyID, "", updateSuccess)
        )
      result.recover {
        case e: Throwable => {
          origin ! responseMessage(companynRequestDto.coyID, e.getMessage, "")
        }
      }

    }

  }
}