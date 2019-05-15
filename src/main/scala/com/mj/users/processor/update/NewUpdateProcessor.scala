package com.mj.users.processor.update

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.mongo.Neo4jConnector.updateNeo4j
import com.mj.users.config.MessageConfig
import com.mj.users.model.JsonRepo._
import com.mj.users.model.{UpdateRequest, responseMessage}
import com.mj.users.mongo.KafkaAccess
import com.mj.users.mongo.UpdateDao.{insertNewUpdate, insertNewUpdateFeed}
import spray.json._


import scala.concurrent.ExecutionContext.Implicits.global

class NewUpdateProcessor extends Actor with MessageConfig with KafkaAccess{

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)

/**
  def receive = {

    case (updateRequestDto: UpdateRequest) => {
      val origin = sender()
      val result = insertNewUpdate(updateRequestDto).map(response =>
        origin ! response
      )

      result.recover {
        case e: Throwable => {
          origin ! responseMessage("", e.getMessage, "")
        }
      }
    }
  }

*/

def receive = {

    case (postRequestDto: UpdateRequest) => {
      val origin = sender()
      val result = insertNewUpdate(postRequestDto).flatMap(postResponse => {
        insertNewUpdateFeed(postResponse, "Update").flatMap(insertNewUpdateFeed => {
          //notificationRoom.notificationActor ! insertNewPostFeed
          sendPostToKafka(insertNewUpdateFeed.toJson.toString)
          val script = s"MERGE (s:feeds {memberID:'${postRequestDto.memberID}', FeedID: '${insertNewUpdateFeed._id}', post_date: TIMESTAMP()})"
          updateNeo4j(script)
          
        }
        ).map(resp => {
            println("postResponse:"+postResponse.toJson.toString)
          //sendPostToKafka(postResponse.toJson.toString)
        }).map(resp => origin ! postResponse)
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
