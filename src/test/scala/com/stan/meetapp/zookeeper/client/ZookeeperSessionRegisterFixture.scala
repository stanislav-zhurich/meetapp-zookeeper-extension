package com.stan.meetapp.zookeeper.client

import scala.util.Try

import org.mockito.Mockito.mock

/**
 * @author Stanislav Zhurich
 */
trait ZookeeperSessionRegisterFixture{
  
   val host = "test_host"
   val addrr = "test_address"
    
    
  def registerFixture = new {
    val sessionMock = mock(classOf[ZookeeperSession])
    val zookeeperSessionRegistry = new ZookeeperSessionRegister(host){
      override lazy val address = addrr
      override val session = Try{sessionMock}
    }
  }
}