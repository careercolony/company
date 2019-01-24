package com.mj.users.config

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by rmanas001c on 1/6/2018
  */
trait MessageConfig {
  //val conf = ConfigFactory.load("messages.conf")
  val conf: Config = ConfigFactory.load("application.conf")

  /* Success Mesages*/
  val updateSuccess = conf.getString("successMessages.updateSuccess")
  val deleteSuccess = conf.getString("successMessages.deleteSuccess")


  /* Error Codes & descriptions*/

  val updateFailed = conf.getString("errorMessages.updateFailed")
  val noRecordFound = conf.getString("errorMessages.noRecordFound")




}

