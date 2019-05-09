package com.mj.users.processor.company

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.util.Timeout
import com.mj.users.config.MessageConfig
import com.mj.users.model.{CompanyRequest, responseMessage}
import com.mj.users.mongo.CompanyDao.insertNewCompany
import com.mj.users.mongo.Neo4jConnector.connectNeo4j

import scala.concurrent.ExecutionContext.Implicits.global

class NewCompanyProcessor extends Actor with MessageConfig{

  implicit val timeout = Timeout(500, TimeUnit.SECONDS)


  def receive = {

    case (companyRequestDto: CompanyRequest) => {
      val origin = sender()
      
      
      //val script = s"MATCH (a:users {memberID:'${followInvitationFriend.memberID}'} ), (b:users {memberID:'${followInvitationFriend.inviteeID}'} ) CREATE (a)-[r:FOLLOW {status:'active'}]->(b)"
      val result = insertNewCompany(companyRequestDto).map(response =>{
        val aboutVal: String = response.about_us match { case None => "" case Some(str) => str }
        val industryVal: String = response.industry match { case None => "" case Some(str) => str }
        val logoVal: String = response.logo match { case None => "" case Some(str) => str }
        
            val script = s"MERGE (c:Company { memberID:'${response.memberID}', coyID: '${response.coyID}', company_name: '${response.company_name}', logo: '${logoVal}', company_url: '${response.company_url}', industry: '${industryVal}', about_us: '${aboutVal}', claimed:TRUE, created_date: TIMESTAMP()})"
            
            connectNeo4j(script).map(resp => resp match {
              case count if count > 0 => origin ! response
              case 0 => origin ! responseMessage("", s"insert record email : ${response.company_name}", "")
            })
      })

      result.recover {
        case e: Throwable => {
          origin ! responseMessage("", e.getMessage, "")
        }
      }
    }
  }
}
