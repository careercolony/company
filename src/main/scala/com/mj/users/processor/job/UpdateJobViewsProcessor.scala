package com.mj.users.processor.job

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{Job, responseMessage}
import com.mj.users.mongo.JobDao.updateJobViews

import scala.concurrent.ExecutionContext.Implicits.global

class UpdateJobViewsProcessor extends Actor with MessageConfig {

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (memberID: String, jobID: String)  => {
      val origin = sender()
      val result = updateJobViews(memberID,jobID)
        .map(response => origin ! responseMessage(jobID, "", updateSuccess)
        )
      result.recover {
        case e: Throwable => {
          origin ! responseMessage(jobID, e.getMessage, "")
        }
      }

    }

  }
}