package com.stan.meetapp.zookeeper.client

import org.apache.zookeeper.data.Stat
import org.apache.zookeeper.ZooKeeper
import scala.util.Try
import org.apache.zookeeper.AsyncCallback.StringCallback


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
   * register node asynchronously
   */
  def runForMasterAsync(path:String, data:String, callback:StringCallback)
  
 
  /**
   * retrieves data for specified path
   */
  def getData  [A <: Any] (path:String)(implicit converter: Array[Byte] => A):Try[Tuple2[A, Stat]]
}