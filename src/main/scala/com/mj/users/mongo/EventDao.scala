package com.mj.users.mongo

import com.mj.users.model._
import com.mj.users.mongo.MongoConnector._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.Future

object EventDao {


  val eventCollection: Future[BSONCollection] = db.map(_.collection[BSONCollection]("events"))

  implicit def eventeWriter = Macros.handler[Event]

  //insert user Details
  def insertNewEvent(userRequest: EventRequest): Future[Event] = {
    for {
      eventData <- Future {
        Event(userRequest.memberID,
          userRequest.coyID,
          BSONObjectID.generate().stringify,
          userRequest.title,
          userRequest.description,
          userRequest.post_type,
          userRequest.thumbnail_url,
          userRequest.event_url,
          userRequest.author,
          userRequest.event_start_date,
          userRequest.event_end_date,
          userRequest.created_date
        )
      }
      response <- insert[Event](eventCollection, eventData)
    }
      yield (response)
  }

  def updateEventDetails(eve: Event , event :Event): Future[String] = {

         val newEventDetails = event.copy(
           memberID = eve.memberID,
           coyID = eve.coyID,
           title = eve.title,
           description = eve.description,
           post_type = eve.post_type,
           thumbnail_url = eve.thumbnail_url,
           event_url = eve.event_url,
           author = eve.author,
           event_start_date = eve.event_start_date,
           event_end_date = eve.event_end_date,
           created_date = eve.created_date
          )
    for {
         msg <- updateDetails(eventCollection, {
           BSONDocument("eventID" -> event.eventID)
         }, newEventDetails).map(resp => resp)
    } yield (msg)
  }


  def getEventDetailsByID(memberID: String): Future[List[Event]] = {
    searchAll[Event](eventCollection,
      document("memberID" -> memberID))
  }

  def getOneEventDetails(memberID: String, eventID: String): Future[Option[Event]] = {
    search[Event](eventCollection,
      document("memberID" -> memberID, "eventID" -> eventID))
  }


}
