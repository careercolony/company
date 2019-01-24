package com.mj.users.tools

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.mj.users.route.company._
import com.mj.users.route.event._
import com.mj.users.route.job._
import com.mj.users.route.update._
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}

object RouteUtils extends NewCompanyRoute with UpdateCompanyRoute with GetCompanyByMemberRoute with GetOneCompanyRoute with DeleteCompnayRoute
  with NewJobRoute with UpdateJobRoute with GetJobByMemberRoute with GetOneJobRoute with DeleteJobRoute
  with NewEventRoute with UpdateEventRoute with GetEventByMemberRoute with GetOneEventRoute with DeleteEventRoute
  with NewUpdateRoute with UpdateRoute with GetUpdateByMemberRoute with GetOneUpdateRoute with DeleteUpdateRoute with AddAdminRoute with AddLocationRoute
  with DeleteLocationRoute with DeleteFieldRoute with UpdateJobViewsRoute {

  /*  createUsersCollection()
    createOnlinesCollection()*/

  def badRequest(request: HttpRequest): StandardRoute = {
    val method = request.method.value.toLowerCase
    val path = request.getUri().path()
    val queryString = request.getUri().rawQueryString().orElse("")
    method match {
      case _ =>
        complete((StatusCodes.NotFound, "404 error, resource not found!"))
    }
  }

  //log duration and request info route
  def logDuration(inner: Route)(implicit ec: ExecutionContext): Route = { ctx =>
    val rejectionHandler = RejectionHandler.default
    val start = System.currentTimeMillis()
    val innerRejectionsHandled = handleRejections(rejectionHandler)(inner)
    mapResponse { resp =>
      val currentTime = new DateTime()
      val currentTimeStr = currentTime.toString("yyyy-MM-dd HH:mm:ss")
      val duration = System.currentTimeMillis() - start
      var remoteAddress = ""
      var userAgent = ""
      var rawUri = ""
      ctx.request.headers.foreach(header => {
        //this setting come from nginx
        if (header.name() == "X-Real-Ip") {
          remoteAddress = header.value()
        }
        if (header.name() == "User-Agent") {
          userAgent = header.value()
        }
        //you must set akka.http.raw-request-uri-header=on config
        if (header.name() == "Raw-Request-URI") {
          rawUri = header.value()
        }
      })
      Future {
        val mapPattern = Seq("user")
        var isIgnore = false
        mapPattern.foreach(pattern =>
          isIgnore = isIgnore || rawUri.startsWith(s"/$pattern"))
        if (!isIgnore) {
          println(
            s"# $currentTimeStr ${ctx.request.uri} [$remoteAddress] [${ctx.request.method.name}] [${resp.status.value}] [$userAgent] took: ${duration}ms")
        }
      }
      resp
    }(innerRejectionsHandled)(ctx)
  }

  def routeRoot(implicit ec: ExecutionContext,
                system: ActorSystem,
                materializer: ActorMaterializer) = {
    routeLogic ~
      extractRequest { request =>
        badRequest(request)
      }
  }


  def routeLogic(implicit ec: ExecutionContext,
                 system: ActorSystem,
                 materializer: ActorMaterializer) = {
    newCompany(system) ~ updateCompany(system) ~ getCompanyByMember(system) ~ GetOneCompany(system) ~ DeleteCompany(system) ~
      newUpdate(system) ~ updateUpdate(system) ~ getUpdateByMember(system) ~ GetOneUpdate(system) ~ DeleteUpdate(system) ~
      newJob(system) ~ updateJob(system) ~ getJobByMember(system) ~ GetOneJob(system) ~ DeleteJob(system) ~
      newEvent(system) ~ updateEvent(system) ~ getEventByMember(system) ~ GetOneEvent(system) ~ DeleteEvent(system) ~ addAdmin(system) ~
      addLocation(system) ~ deleteLocation(system) ~ deleteField(system) ~ updateJobViews(system)
  }

  def logRoute(implicit ec: ExecutionContext,
               system: ActorSystem,
               materializer: ActorMaterializer) = logDuration(routeRoot)
}
