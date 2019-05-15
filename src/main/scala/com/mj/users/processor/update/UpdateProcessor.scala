package com.mj.users.processor.update

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{Update, responseMessage}
import com.mj.users.mongo.UpdateDao.{updateUpdateDetails, updateUpdateFeed}

import scala.concurrent.ExecutionContext.Implicits.global

class UpdateProcessor extends Actor with MessageConfig {

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)

  def receive = {

    case (updateDto: Update) => {
      val origin = sender()
      val result = updateUpdateDetails(updateDto).map(updateResponse => {
        updateUpdateFeed(updateDto, "Update").map(resp =>
          origin ! responseMessage(updateDto.updateID, "", updateSuccess))
      }
      )

      result.recover {
        case e: Throwable => {
          origin ! responseMessage(updateDto.updateID, e.getMessage, "")
        }
      }
    }
  }


  
  /**
  def receive = {

    case (updatenRequestDto: Update) => {
      val origin = sender()

      val result = getOneUpdateDetails(updatenRequestDto.memberID, updatenRequestDto.updateID).map(updateDetails =>
        updateDetails match {
          case None => origin ! responseMessage("", noRecordFound, "")
          case Some(update) =>
            updateUpdateDetails(updatenRequestDto, update).map(response => origin ! responseMessage(updatenRequestDto.updateID, "", updateSuccess))
        }
      )

      result.recover {
        case e: Throwable => {
          origin ! responseMessage(updatenRequestDto.updateID, e.getMessage, "")
        }
      }

    }

  }
  */
}