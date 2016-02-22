package com.stan.meetapp.zookeeper.client

import java.util.concurrent.Executors
import scala.annotation.tailrec
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import org.apache.zookeeper.KeeperException.ConnectionLossException
import org.apache.zookeeper.KeeperException.NoNodeException
import org.apache.zookeeper.KeeperException.NodeExistsException
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.data.Stat
import akka.actor.Extension

/**
 * @author Stanislav Zhurich
 */
class ZookeeperSessionRegister private[client](url:String) extends SessionRegister[ZookeeperSession] 
    with Logging with Extension{
  
  val envVariable = "NODE_ADDRESS"
  val path = "/seed"
  
  override val session = ZookeeperSession(url, 10000, watcher, false)
  
  override lazy val address = {
    val address = System.getenv(envVariable) 
    if(address != null){
      address
    }else{
      java.net.InetAddress.getLocalHost().getHostAddress
    }
  }
  
  def watcher = new Watcher(){
    def process(event:WatchedEvent){}
  }
  
  
  override def registerSeed(implicit context:ExecutionContext):Future[Boolean] = {
   
    @tailrec
    def register():Boolean = {
       session.flatMap { _.runForMaster(path, address) } match {
       case Success(path) =>  {
         log.info("succeedded to register seed node")
         true
       }
       case Failure(e:NodeExistsException) => {
         log.info("seed node was already registered")
         false
       }
       case Failure(e:ConnectionLossException) => {
         log.warn("connection was lost registering seed znode")
         if(checkSeed()) true else register()
       }
       case Failure(e) => {
          log.error(s"unknown exception trying register seed path", e)
          false
       }
     } 
    }
    
    Future{
      register()
    }
  }
  
  override def getSeed:Option[Seed] = {
    session.flatMap { _.getData(path) } match {
      case Success((bytes, stat)) => Some(Seed("", 0))
    }
  }
 
 private[client] def checkSeed():Boolean = {
   session.flatMap { _.getData(path) } match {
      case Success((bytes, stat)) => new String(bytes, "utf-8") == address
      case Failure(e: NoNodeException) => {
         log.warn(s"zookeeper znode was not found for path [%path]")
         false
      }
      case Failure(e:ConnectionLossException) => {
        log.error(s"connection was lost checking path [$path]", e)
        checkSeed()
      }     
      case Failure(e) => {
        log.error(s"unknown exception checking path [$path]", e)
        false
      }
   }
 }
 
}

object ZookeeperSessionRegister{
  def apply(url:String) = new ZookeeperSessionRegister(url)
  implicit def bytesToString(bytes:Array[Byte]):String = new String(bytes, "utf-8")  
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(1))
}