package com.stan.meetapp.zookeeper.client

import scala.util.Try
import org.apache.zookeeper.data.Stat
import scala.concurrent.Future
import scala.concurrent.ExecutionContext


case class Seed(host:String, port:Int)

/**
 * @author Stanislav Zhurich
 */
trait SessionRegister[A <: Session] {
  
  val address:String
  
  val session:Try[A]
  
  def registerSeed(implicit context:ExecutionContext):Future[Boolean]
  
  def getSeed: Option[Seed]
  
}