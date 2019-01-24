package com.mj.users.mongo

import com.mj.users.model._
import com.mj.users.mongo.MongoConnector._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.Future

object JobDao {


  val jobCollection: Future[BSONCollection] = db.map(_.collection[BSONCollection]("jobs"))


  implicit def jobWriter = Macros.handler[Job]


  //insert user Details
  def insertNewJob(userRequest: JobRequest): Future[Job] = {
    for {
      jobData <- Future {
        Job(userRequest.memberID,
          userRequest.coyID,
          BSONObjectID.generate().stringify,
          userRequest.company_name,
          userRequest.company_url,
          userRequest.about_us,
          userRequest.company_size,
          userRequest.logo,
          userRequest.title,
          userRequest.job_description,
          userRequest.job_function,
          userRequest.industry,
          userRequest.job_location,
          userRequest.cover_image,
          userRequest.employment_type,
          userRequest.level,
          userRequest.views
        )
      }
      response <- insert[Job](jobCollection, jobData)
    }
      yield (response)
  }

  def updateJobDetails(com: Job, jobDetails: Job): Future[String] = {
    val newJobDetails = jobDetails.copy(
      memberID = com.memberID,
      coyID = com.coyID,
      company_name = com.company_name,
      company_url = com.company_url,
      about_us = com.about_us,
      company_size = com.company_size,
      logo = com.logo,
      title = com.title,
      job_description = com.job_description,
      job_function = com.job_function,
      industry = com.industry,
      job_location = com.job_location,
      cover_image = com.cover_image,
      employment_type = com.employment_type,
      level = com.level,
      views = com.views
    )

    for {

      msg <- updateDetails(jobCollection, {
        BSONDocument("jobID" -> com.jobID)
      }, newJobDetails)
    } yield (msg)
  }

  def updateJobViews(memberID: String, jobID: String): Future[String] = {

    val result = for {
      response <- update(jobCollection, BSONDocument("jobID" -> jobID),
        BSONDocument("$addToSet" -> BSONDocument("views" -> memberID)))
    }
      yield (response)

    result.recover {
      case e: Throwable => {
        println("msg:" + e.getMessage)
        throw new Exception(e.getMessage)
      }
    }


  }


  def getJobDetailsByID(memberID: String): Future[List[Job]] = {
    searchAll[Job](jobCollection,
      document("memberID" -> memberID))
  }

  def getOneJobDetails(memberID: String, jobID: String): Future[Option[Job]] = {
    search[Job](jobCollection,
      document("memberID" -> memberID, "jobID" -> jobID))
  }


}
