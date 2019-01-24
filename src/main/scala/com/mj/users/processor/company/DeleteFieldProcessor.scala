package com.mj.users.processor.company

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.responseMessage
import com.mj.users.mongo.CompanyDao.companyCollection
import com.mj.users.mongo.MongoConnector._
import reactivemongo.bson.BSONDocument


import scala.concurrent.Future

class DeleteFieldProcessor extends Actor with MessageConfig {

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (field: String, value: String) => {
      val origin = sender()
      val result = remove(companyCollection, if (value forall Character.isDigit) BSONDocument(field -> value.toInt) else BSONDocument(field -> value))
        .flatMap(upResult => Future {
          responseMessage("", "", deleteSuccess)
        }).map(response => origin ! response)

      result.recover {
        case e: Throwable => {
          origin ! responseMessage("", e.getMessage, "")
        }
      }
    }
  }
}
