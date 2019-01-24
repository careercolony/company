package com.mj.users.processor.company

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{CompanyRequest, responseMessage}
import com.mj.users.mongo.CompanyDao.insertNewCompany

import scala.concurrent.ExecutionContext.Implicits.global

class NewCompanyProcessor extends Actor with MessageConfig{

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (companyRequestDto: CompanyRequest) => {
      val origin = sender()
      val result = insertNewCompany(companyRequestDto).map(response =>
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
