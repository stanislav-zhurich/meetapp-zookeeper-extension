package com.stan.meetapp.zookeeper.client

import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import scala.util.Success
import com.stan.meetapp.zookeeper.akka.extension.ZookeeperExtension
import akka.actor.ActorSystem
import org.apache.zookeeper.AsyncCallback.StringCallback

object Test extends App{
  
  implicit val system = ActorSystem("gateway")
  implicit val context = system.dispatcher
  val z = ZookeeperExtension(system)
  z.registerSeedAsync(new StringCallback{
    override def processResult(rc:Int, path:String, ctx:AnyRef, name:String){
      println(s"$rc $path $ctx $name")
    }
  })
  
  
  
  val watcher = new Watcher(){
    override def process(event:WatchedEvent){
       println(event) 
    }
  }
  
 // val registry = ZookeeperSessionRegister("ecsc00103d58.epam.com:2181")
 
  Thread.sleep(100000)
}