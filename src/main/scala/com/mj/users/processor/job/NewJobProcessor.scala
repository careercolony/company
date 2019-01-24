package com.mj.users.processor.job

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{JobRequest, responseMessage}
import com.mj.users.mongo.JobDao.insertNewJob

import scala.concurrent.ExecutionContext.Implicits.global

class NewJobProcessor extends Actor with MessageConfig{

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (jobRequestDto: JobRequest) => {
      val origin = sender()
      val result = insertNewJob(jobRequestDto).map(response =>
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
