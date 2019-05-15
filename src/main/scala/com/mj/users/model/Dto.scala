package com.mj.users.model

import java.util.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.collection.mutable.MutableList


case class LocationRequest(coyID:String, location:Location )

case class Location( address:String, city: String, state: String, country:String)

case class Admin(memberID:String, coyID:String, admin_fname: String, admin_lname: String, admin_email: String, role: String)

case class CompanyRequest(memberID: String, company_name: String, company_url: String, about_us:Option[String], company_size:Option[Int],
                   location: Option[List[Location]], cover_image: Option[String], logo:Option[String], industry:Option[String], company_type:Option[String], year_established:Option[Int], admin: Option[List[Admin]], followers: Option[List[String]], staff: Option[List[String]]
                  )
case class Company(memberID: String, coyID: String, company_name: String, company_url: String, about_us:Option[String], company_size:Option[Int],
                      location:Option[List[Location]], cover_image: Option[String], logo:Option[String], industry:Option[String], company_type:Option[String], year_established:Option[Int], admin: Option[List[Admin]], followers: Option[List[String]], staff: Option[List[String]]
                     )

case class JobRequest(memberID:String, coyID:String, company_name: String, company_url: String, about_us:String, company_size:Int, logo: String, title:String, job_description:String, job_function: String, industry: String, job_location:String, cover_image: String, employment_type: String, level: Option[String], views: Option[List[String]])
case class Job(memberID:String, coyID:String, jobID: String, company_name: String, company_url: String, about_us:String, company_size:Int, logo: String, title:String, job_description:String, job_function: String, industry: String, job_location:String, cover_image: String, employment_type: String, level: Option[String], views: Option[List[String]])

//case class UpdateRequest(memberID:String, coyID:String, title: String, description: String, post_type:String, thumbnail_url:String, update_url:String, author:String, update_date: Option[String])
//case class Update(memberID:String, coyID:String, updateID: String, title: String, description: String, post_type:String, thumbnail_url:String, update_url:String, author:String, update_date: Option[String])

case class UpdateRequest(memberID: String, coyID: String, title: Option[String],
                       description: Option[String], message: Option[String], post_type: Option[String],
                       author: Option[String], author_avatar: Option[String], author_position: Option[String],
                       author_current_employer: Option[String], thumbnail_url: Option[List[String]],
                       provider_name: Option[String], provider_url: Option[String],
                       update_url: Option[String], html: Option[String],
                       readers: Option[List[String]])

case class Update(memberID: String, coyID: String, status : String ,updateID: String, post_date: String,updated_date : String , title: Option[String],
                description: Option[String], message: Option[String], post_type: Option[String],
                author: Option[String], author_avatar: Option[String], author_position: Option[String],
                author_current_employer: Option[String], thumbnail_url: Option[List[String]],
                provider_name: Option[String], provider_url: Option[String],
                update_url: Option[String], html: Option[String],
                readers: Option[List[String]], likes: Option[List[LikeDetails]], Shares: Option[List[String]])

case class LikeDetails(likerID: String, like: Option[String], like_date: String)

case class Feed(_id: String, memberID: String, activityType: String, postDetails: Update, actorID: Option[String], actorName: Option[String], actorAvatar: Option[String], commentID: Option[String])


case class EventRequest(memberID:String, coyID:String, title: String, description: String, post_type:String, thumbnail_url:String, event_url:String, author:String, event_start_date: Option[String], event_end_date: Option[String], created_date:String)
case class Event(memberID:String, coyID:String, eventID: String, title: String, description: String, post_type:String, thumbnail_url:String, event_url:String, author:String, event_start_date: Option[String], event_end_date: Option[String], created_date:String)



//Response format for all apis
case class responseMessage(uid: String, errmsg: String, successmsg: String)

object JsonRepo extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val adminFormats: RootJsonFormat[Admin] = jsonFormat6(Admin)
  implicit val locationFormats: RootJsonFormat[Location] = jsonFormat4(Location)
  implicit val locationRequestFormats: RootJsonFormat[LocationRequest] = jsonFormat2(LocationRequest)

  implicit val CompanyequestDtoFormats: RootJsonFormat[CompanyRequest] = jsonFormat14(CompanyRequest)
  implicit val CompanyResponseDtoFormats: RootJsonFormat[Company] = jsonFormat15(Company)
  implicit val JobRequestDtoFormats: RootJsonFormat[JobRequest] = jsonFormat16(JobRequest)
  implicit val JobResponseDtoFormats: RootJsonFormat[Job] = jsonFormat17(Job)

  implicit val UpdateRequestequestDtoFormats: RootJsonFormat[UpdateRequest] = jsonFormat16(UpdateRequest)
  implicit val UpdateResponseDtoFormats: RootJsonFormat[Update] = jsonFormat22(Update)
  implicit val LikeDtoFormats: RootJsonFormat[LikeDetails] = jsonFormat3(LikeDetails)
  implicit val feedFormats: RootJsonFormat[Feed] = jsonFormat8(Feed)
  
  implicit val EventRequestequestDtoFormats: RootJsonFormat[EventRequest] = jsonFormat11(EventRequest)
  implicit val EventResponseDtoFormats: RootJsonFormat[Event] = jsonFormat12(Event)

  implicit val errorMessageDtoFormats: RootJsonFormat[responseMessage] = jsonFormat3(responseMessage)
}
