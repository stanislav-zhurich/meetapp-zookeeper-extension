package com.stan.meetapp.zookeeper.client

import org.slf4j.LoggerFactory

trait Logging {
  
  val log = LoggerFactory.getLogger(this.getClass);
  
  def info(message:String){log.info(message)}
  
  def error(message:String){log.error(message)}
  
  def error(message:String, e:Throwable){log.error(message, e)}
}