package com.mj.users.processor.company

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.responseMessage
import com.mj.users.mongo.CompanyDao.getCompanyDetailsByID

import scala.concurrent.ExecutionContext.Implicits.global

class GetCompanyByMemberProcessor extends Actor with MessageConfig{

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (memberID: String) => {
      val origin = sender()
      val result = getCompanyDetailsByID(memberID).map(response =>
        response match {
          case List() => origin ! responseMessage("", noRecordFound, "")
          case _ =>  origin ! response
        }
      )

      result.recover {
        case e: Throwable => {
          origin ! responseMessage("", e.getMessage, "")
        }
      }
    }
  }
}
