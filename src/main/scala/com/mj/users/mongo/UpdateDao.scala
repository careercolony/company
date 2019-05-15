package com.mj.users.mongo

import com.mj.users.model._
import com.mj.users.mongo.MongoConnector._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._
import org.joda.time.DateTime
import com.mj.users.config.Application._

import scala.concurrent.Future

object UpdateDao {


  val updateCollection: Future[BSONCollection] = db.map(_.collection[BSONCollection]("updates"))
  val feedCollection: Future[BSONCollection] = db.map(_.collection[BSONCollection]("feed"))

  implicit def likeDetailsWriter = Macros.handler[LikeDetails]
  implicit def updateWriter = Macros.handler[Update]
  implicit def feedWriter = Macros.handler[Feed]

  
  //insert user Details
  def insertNewUpdate(userRequest: UpdateRequest): Future[Update] = {
    for {
      updateData <- Future {
        Update(userRequest.memberID,
          userRequest.coyID,
          active,
          BSONObjectID.generate().stringify,
          DateTime.now.toString("yyyy-MM-dd'T'HH:mm:ssZ"),"",
          userRequest.title,
          userRequest.description,
          userRequest.message,
          userRequest.post_type,
          userRequest.author,
          userRequest.author_avatar,
          userRequest.author_position,
          userRequest.author_current_employer,
          userRequest.thumbnail_url,
          userRequest.provider_name,
          userRequest.provider_url,
          userRequest.update_url,
          userRequest.html,
          userRequest.readers,
          None,
          None
        )
      }
      response <- insert[Update](updateCollection, updateData)
    }
      yield (response)
  }

/**
  def updateUpdateDetails(com: Update, updateJobDetails: Update): Future[String] = {
    val newUpdateDetails = updateJobDetails.copy(
      memberID = com.memberID,
      coyID = com.coyID,
      title = com.title,
      description = com.description,
      post_type = com.post_type,
      thumbnail_url = com.thumbnail_url,
      update_url = com.update_url,
      author = com.author,
      post_date = com.post_date
    )
    for {

      msg <- updateDetails(updateCollection, {
        BSONDocument("updateID" -> com.updateID)
      }, newUpdateDetails)

    } yield (msg)
  }
*/
    //update user Details
  def updateUpdateDetails(userRequest: Update): Future[String] = {
    for {

      response <- updateDetails[Update](updateCollection, BSONDocument("updateID" -> userRequest.updateID), userRequest.copy(updated_date =  DateTime.now.toString("yyyy-MM-dd'T'HH:mm:ssZ")))
    }
      yield (response)
  }


  def getUpdateDetailsByID(memberID: String): Future[List[Update]] = {
    searchAll[Update](updateCollection,
      document("memberID" -> memberID))
  }

  def getOneUpdateDetails(memberID: String, updateID: String): Future[Option[Update]] = {
    search[Update](updateCollection,
      document("memberID" -> memberID, "updateID" -> updateID))
  }

  def insertNewUpdateFeed(userRequest: Update, feedType: String): Future[Feed] = {
    for {
      feedData <- Future {
        Feed(BSONObjectID.generate().stringify, userRequest.memberID,
          feedType,
          Update(userRequest.memberID,userRequest.coyID,active,
            userRequest.updateID,
            DateTime.now.toString("yyyy-MM-dd'T'HH:mm:ssZ"),"",
            userRequest.title, userRequest.description, userRequest.message, userRequest.post_type,
            userRequest.author, userRequest.author_avatar, userRequest.author_position,
            userRequest.author_current_employer, userRequest.thumbnail_url, userRequest.provider_name, userRequest.provider_url,
            userRequest.update_url, userRequest.html, userRequest.readers, None,
            None
          ),
          None, None, None, None
        )
      }
      response <- insert[Feed](feedCollection, feedData)
    }
      yield (response)


  }

  

  //update user Details
  def updateUpdateFeed(userRequest: Update, feedType: String): Future[String] = {

    val selector = BSONDocument("postDetails.updateID" -> userRequest.updateID, "activityType" -> "Update")
    val result = for {

      response <- update(feedCollection, selector, BSONDocument(
        "$set" -> BSONDocument("postDetails" -> userRequest)
      ))
    }
      yield (response)

    result.recover {
      case e: Throwable => {
        println("msg:" + e.getMessage)
        throw new Exception(e.getMessage)
      }


    }
  }


}
