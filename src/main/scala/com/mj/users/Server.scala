package com.mj.users

import java.net.InetAddress

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import com.mj.users.config.Application
import com.mj.users.config.Application._
import com.mj.users.tools.CommonUtils._
import com.mj.users.tools.RouteUtils
import com.typesafe.config.ConfigFactory


object Server extends App {
  val seedNodesStr = seedNodes
    .split(",")
    .map(s => s""" "akka.tcp://users-cluster@$s" """)
    .mkString(",")

  val inetAddress = InetAddress.getLocalHost
  var configCluster = Application.config.withFallback(
    ConfigFactory.parseString(s"akka.cluster.seed-nodes=[$seedNodesStr]"))

  configCluster = configCluster
    .withFallback(
      ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname=$hostName"))
    .withFallback(
      ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$akkaPort"))

  implicit val system: ActorSystem = ActorSystem("users-cluster", configCluster)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  //Company
  val newCompanyProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.company.NewCompanyProcessor]), "newCompanyProcessor")
  val updateCompanyProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.company.UpdateCompanyProcessor]), "updateCompanyProcessor")
  val getCompanyByMemberProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.company.GetCompanyByMemberProcessor]), "getCompanyByMemberProcessor")
  val getOneCompanyProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.company.GetOneCompanyProcessor]), "getOneCompanyProcessor")
  val deleteCompanyProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.company.DeleteCompanyProcessor]), "deleteCompanyProcessor")

  //Update
  val newUpdateProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.update.NewUpdateProcessor]), "newUpdateProcessor")
  val updateUpdateProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.update.UpdateProcessor]), "updateProcessor")
  val getUpdateByMemberProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.update.GetUpdateByMemberProcessor]), "getUpdateByMemberProcessor")
  val getOneUpdateProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.update.GetOneUpdateProcessor]), "getOneUpdateProcessor")
  val deleteUpdateProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.update.DeleteUpdateProcessor]), "deleteUpdateProcessor")

  //Event
  val newEventProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.event.NewEventProcessor]), "newEventProcessor")
  val updateEventProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.event.UpdateEventProcessor]), "updateEventProcessor")
  val getEventByMemberProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.event.GetEventByMemberProcessor]), "getEventByMemberProcessor")
  val getOneEventProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.event.GetOneEventProcessor]), "getOneEventProcessor")
  val deleteEventProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.event.DeleteEventProcessor]), "deleteEventProcessor")

  //Job
  val newJobProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.job.NewJobProcessor]), "newJobProcessor")
  val updateJobProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.job.UpdateJobProcessor]), "updateJobProcessor")
  val getJobByMemberProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.job.GetJobByMemberProcessor]), "getJobByMemberProcessor")
  val getOneJobProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.job.GetOneJobProcessor]), "getOneJobProcessor")
  val deleteJobProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.job.DeleteJobProcessor]), "deleteJobProcessor")


  val addAdminProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.company.AddAdminProcessor]), "addAdminProcessor")
  val addLocationProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.company.AddLocationProcessor]), "addLocationProcessor")
  val deleteLocationProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.company.DeleteLocationProcessor]), "deleteLocationProcessor")
  val deleteFieldProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.company.DeleteFieldProcessor]), "deleteFieldProcessor")
  val updateJobViewsProcessor = system.actorOf(RoundRobinPool(poolSize).props(Props[processor.job.UpdateJobViewsProcessor]), "updateJobViewsProcessor")

  import system.dispatcher

  Http().bindAndHandle(RouteUtils.logRoute, "0.0.0.0", port)

  consoleLog("INFO",
    s"User server started! Access url: https://$hostName:$port/")
}

