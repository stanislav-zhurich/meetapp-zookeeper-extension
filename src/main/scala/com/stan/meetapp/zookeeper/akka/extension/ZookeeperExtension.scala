package com.stan.meetapp.zookeeper.akka.extension

import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import com.stan.meetapp.zookeeper.client.ZookeeperSessionRegister
import akka.actor.ExtendedActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem

/**
 * @author Stanislav Zhurich
 */
object ZookeeperExtension extends ExtensionId[ZookeeperSessionRegister]  with ExtensionIdProvider{
  
  override def lookup = ZookeeperExtension
  
  override def createExtension(system: ExtendedActorSystem) = {
    val config = ConfigFactory.load();
    val zookeeperUrl = config.getString("akka.zookeeper.path")
    ZookeeperSessionRegister(zookeeperUrl)
  }
  
  override def get(system: ActorSystem): ZookeeperSessionRegister = super.get(system)
}