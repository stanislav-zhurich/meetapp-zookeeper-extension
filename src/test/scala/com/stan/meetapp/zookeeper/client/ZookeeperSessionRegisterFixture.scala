package com.stan.meetapp.zookeeper.client

import scala.util.Try

import org.mockito.Mockito.mock

/**
 * @author Stanislav Zhurich
 */
trait ZookeeperSessionRegisterFixture{
  
   val host = "test_host"
   //val seedPath = "path:123"
    
    
  def registerFixture = new {
    val seedPath = "path:123"
    val sessionMock = mock(classOf[ZookeeperSession])
    val zookeeperSessionRegistry = new ZookeeperSessionRegister(host, seedPath){
      override val session = Try{sessionMock}
    }
  }
}