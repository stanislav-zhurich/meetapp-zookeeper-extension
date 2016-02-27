package com.stan.meetapp.zookeeper.client

import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooKeeper
import scala.util.Try
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE
import org.apache.zookeeper.data.Stat
import org.apache.zookeeper.AsyncCallback.StringCallback

/**
 * @author Stanislav Zhurich
 */
class ZookeeperSession private[client] (url:String,
    timeout:Int = 10000, watcher:Watcher, canBeReadOnly:Boolean = false) extends Session with Logging{
  
  info(s"creating zookeeper session on host $url" )
  
  private[client] lazy val zookeeper = new ZooKeeper(url, timeout, watcher, canBeReadOnly);
  
  override def close(){
    zookeeper.close()
  }
  
  override def runForMaster(path:String, data:String):Try[String] = {
    info(s"trying to set master in $path")
    Try(zookeeper.create(path, data.getBytes, OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL));
  }
  
  override def runForMasterAsync(path:String, data:String, callback:StringCallback){
    info(s"trying to set master in $path")
    zookeeper.create(path, data.getBytes, OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, callback, new Object())
  }
  
  override def getData  [A <: Any] (path:String)(implicit converter: Array[Byte] => A):Try[Tuple2[A, Stat]] = {
    val stat = new Stat
    Try{
      val data = zookeeper.getData(path, false, stat)
      val convertedData = converter(data)
      (convertedData, stat)
    }
  }
}

object ZookeeperSession{
  def apply(url:String, timeout:Int = 10000, watcher:Watcher, canBeReadOnly:Boolean = false) = 
    Try(new ZookeeperSession(url, timeout, watcher, canBeReadOnly)) 
}