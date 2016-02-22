package com.stan.meetapp.zookeeper.client

import org.apache.zookeeper.Watcher
import org.apache.zookeeper.WatchedEvent
import akka.actor.ActorSystem
import com.stan.meetapp.zookeeper.akka.extension.ZookeeperExtension

object Test extends App{
  
  implicit val system = ActorSystem("gateway")
  ZookeeperExtension(system).registerSeed(system.dispatcher)
  
  val watcher = new Watcher(){
    override def process(event:WatchedEvent){
       println(event) 
    }
  }
  
 // val registry = ZookeeperSessionRegister("ecsc00103d58.epam.com:2181")
 
  Thread.sleep(100000)
}