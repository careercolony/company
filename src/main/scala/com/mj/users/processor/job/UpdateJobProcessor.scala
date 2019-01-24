package com.mj.users.processor.job

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{Job, responseMessage}
import com.mj.users.mongo.JobDao.{getOneJobDetails, updateJobDetails}

import scala.concurrent.ExecutionContext.Implicits.global

class UpdateJobProcessor extends Actor with MessageConfig {

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (jobnRequestDto: Job) => {
      val origin = sender()

      val result = getOneJobDetails(jobnRequestDto.memberID, jobnRequestDto.jobID).map(jobDetails =>
        jobDetails match {
          case None => origin ! responseMessage("", noRecordFound, "")
          case Some(job) =>
            updateJobDetails(jobnRequestDto,jobnRequestDto).map(response => origin ! responseMessage(jobnRequestDto.jobID, "", updateSuccess))
        }
      )

      result.recover {
        case e: Throwable => {
          origin ! responseMessage(jobnRequestDto.jobID, e.getMessage, "")
        }
      }

    }

  }
}