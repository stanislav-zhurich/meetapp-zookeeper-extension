package com.stan.meetapp.zookeeper.client

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Try
import org.apache.zookeeper.AsyncCallback.StringCallback


case class Seed(path:String){
  
  def getPathPort:Option[Tuple2[String, Int]] = {
    path.split(":") match {
      case Array(s:String, i:String) => Some((s, i.toInt))
      case _ => None
    }
  }
}

/**
 * @author Stanislav Zhurich
 */
trait SessionRegister[A <: Session] {
  
  val session:Try[A]
  
  def registerSeed(implicit context:ExecutionContext):Future[Boolean]
  
  def registerSeedAsync(callback:StringCallback):Unit
  
  def getSeed(implicit context:ExecutionContext): Future[Seed]
  
}