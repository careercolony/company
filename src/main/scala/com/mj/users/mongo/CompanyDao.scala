package com.mj.users.mongo

import com.mj.users.model._
import com.mj.users.mongo.MongoConnector._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.Future

object CompanyDao {


  val companyCollection: Future[BSONCollection] = db.map(_.collection[BSONCollection]("company"))

  implicit def adminWriter = Macros.handler[Admin]

  implicit def LocationWriter = Macros.handler[Location]

  implicit def companyeWriter = Macros.handler[Company]


  //insert user Details
  def insertNewCompany(userRequest: CompanyRequest): Future[Company] = {
    for {
      companyData <- Future {
        Company(userRequest.memberID,
          BSONObjectID.generate().stringify,
          userRequest.company_name,
          userRequest.company_url,
          userRequest.about_us,
          userRequest.company_size,
          userRequest.location,
          userRequest.cover_image,
          userRequest.logo,
          userRequest.industry,
          userRequest.company_type,
          userRequest.year_established,
          userRequest.admin,
          userRequest.followers,
          userRequest.staff
        )
      }
      response <- insert[Company](companyCollection, companyData)
    }
      yield (response)
  }

  def updateCompanyDetails(com: Company): Future[String] = {
    for {companyDetails <- getOneCompanyDetails(com.memberID, com.coyID)
         newCompanyDetails = companyDetails.get.copy(
           memberID = com.memberID,
           company_name = com.company_name,
           company_url = com.company_url,
           about_us = com.about_us,
           company_size = com.company_size,
           location = com.location,
           cover_image = com.cover_image,
           logo = com.logo,
           industry = com.industry,
           company_type = com.company_type,
           year_established = com.year_established,
           admin = com.admin,
           followers = com.followers,
           staff = com.staff
         )
         msg <- updateDetails(companyCollection, {
           BSONDocument("coyID" -> com.coyID)
         }, newCompanyDetails)
    } yield (msg)
  }

  def updateAdmin(adminRequestDto: Admin): Future[String] = {

    val result = for {
      response <- update(companyCollection, BSONDocument("coyID" -> adminRequestDto.coyID),
        BSONDocument("$addToSet" ->
          BSONDocument("admin" ->
            BSONDocument("memberID" -> adminRequestDto.memberID, "admin_fname" -> adminRequestDto.admin_fname, "admin_lname" -> adminRequestDto.admin_lname, "admin_email" -> adminRequestDto.admin_email,
              "role" -> adminRequestDto.role , "coyID" -> adminRequestDto.coyID))))
    }
      yield (response)

    result.recover {
      case e: Throwable => {
        println("msg:" + e.getMessage)
        throw new Exception(e.getMessage)
      }
    }
  }

  def updateLocation(locationRequestDto: LocationRequest): Future[String] = {

    val result = for {
      response <- update(companyCollection, BSONDocument("coyID" -> locationRequestDto.coyID),
        BSONDocument("$addToSet" -> BSONDocument("location"->locationRequestDto.location)))
    }
      yield (response)

    result.recover {
      case e: Throwable => {
        println("msg:" + e.getMessage)
        throw new Exception(e.getMessage)
      }
    }
  }

  def deleteLocation(coyID: String, address: String): Future[String] = {

    val result = for {
      response <- update(companyCollection, BSONDocument("coyID" -> coyID),
        BSONDocument("$pull" -> BSONDocument("location"->BSONDocument("address"->address))))
    }
      yield (response)

    result.recover {
      case e: Throwable => {
        println("msg:" + e.getMessage)
        throw new Exception(e.getMessage)
      }
    }
  }


  def getCompanyDetailsByID(memberID: String): Future[List[Company]] = {
    searchAll[Company](companyCollection,
      document("memberID" -> memberID))
  }

  def getOneCompanyDetails(memberID: String, comID: String): Future[Option[Company]] = {
    search[Company](companyCollection,
      document("memberID" -> memberID, "coyID" -> comID))
  }


}
