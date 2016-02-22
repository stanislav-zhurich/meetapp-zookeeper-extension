package com.stan.meetapp.zookeeper.client

import org.apache.zookeeper.data.Stat
import org.apache.zookeeper.ZooKeeper
import scala.util.Try


/**
 * 
 */
trait Session {
  
  /**
   * close the active session
   */
  def close
  
  /**
   * register current node as master
   */
  def runForMaster(path:String, data:String):Try[String]
  
 
  /**
   * retrieves data for specified path
   */
  def getData  [A <: Any] (path:String)(implicit converter: Array[Byte] => A):Try[Tuple2[A, Stat]]
}