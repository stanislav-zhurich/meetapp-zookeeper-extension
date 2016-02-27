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
import org.apache.zookeeper.AsyncCallback.StringCallback

/**
 * @author Stanislav Zhurich
 */
class ZookeeperSessionRegister private[client](url:String, seedPath:String) extends SessionRegister[ZookeeperSession] 
    with Logging with Extension{
  
  val pathName = "/seed"
  
  override val session = ZookeeperSession(url, 10000, null, false)
  
  override def registerSeedAsync(callback:StringCallback):Unit = {
    session map {case s => {
      s.runForMasterAsync(pathName, seedPath, callback)
    }}
  }
  
  override def registerSeed(implicit context:ExecutionContext):Future[Boolean] = {
   
    @tailrec
    def register():Boolean = {
       session.flatMap { _.runForMaster(pathName, seedPath) } match {
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
  
  override def getSeed(implicit context:ExecutionContext):Future[Seed] = {
   
    def seed:Seed = session.flatMap {  _.getData(pathName) } match {
            case Success((bytes, stat)) if (new String(bytes, "utf-8") == seedPath) => Seed(seedPath)
    }
    
    Future(seed)
  }
 
 private[client] def checkSeed():Boolean = {
   session.flatMap { _.getData(pathName) } match {
      case Success((bytes, stat)) => {
        new String(bytes, "utf-8") == seedPath
      }
      case Failure(e: NoNodeException) => {
         log.warn(s"zookeeper znode was not found for path [%path]")
         false
      }
      case Failure(e:ConnectionLossException) => {
        log.error(s"connection was lost checking path [$pathName]", e)
        checkSeed()
      }     
      case Failure(e) => {
        log.error(s"unknown exception checking path [$pathName]", e)
        false
      }
   }
 }
 
}

object ZookeeperSessionRegister{
  def apply(url:String, seedPath:String) = new ZookeeperSessionRegister(url, seedPath)
  implicit def bytesToString(bytes:Array[Byte]):String = new String(bytes, "utf-8")  
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(1))
}