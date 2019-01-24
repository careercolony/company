package com.mj.users.mongo

import com.mj.users.model._
import com.mj.users.mongo.MongoConnector._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.Future

object UpdateDao {


  val updateCollection: Future[BSONCollection] = db.map(_.collection[BSONCollection]("updates"))

  implicit def updateWriter = Macros.handler[Update]


  //insert user Details
  def insertNewUpdate(userRequest: UpdateRequest): Future[Update] = {
    for {
      updateData <- Future {
        Update(userRequest.memberID,
          BSONObjectID.generate().stringify,
          userRequest.title,
          userRequest.description,
          userRequest.post_type,
          userRequest.thumbnail_url,
          userRequest.update_url,
          userRequest.author,
          userRequest.update_date
        )
      }
      response <- insert[Update](updateCollection, updateData)
    }
      yield (response)
  }

  def updateUpdateDetails(com: Update, updateJobDetails: Update): Future[String] = {
    val newUpdateDetails = updateJobDetails.copy(
      memberID = com.memberID,
      title = com.title,
      description = com.description,
      post_type = com.post_type,
      thumbnail_url = com.thumbnail_url,
      update_url = com.update_url,
      author = com.author,
      update_date = com.update_date
    )
    for {

      msg <- updateDetails(updateCollection, {
        BSONDocument("updateID" -> com.updateID)
      }, newUpdateDetails)

    } yield (msg)
  }


  def getUpdateDetailsByID(memberID: String): Future[List[Update]] = {
    searchAll[Update](updateCollection,
      document("memberID" -> memberID))
  }

  def getOneUpdateDetails(memberID: String, updateID: String): Future[Option[Update]] = {
    search[Update](updateCollection,
      document("memberID" -> memberID, "updateID" -> updateID))
  }


}
