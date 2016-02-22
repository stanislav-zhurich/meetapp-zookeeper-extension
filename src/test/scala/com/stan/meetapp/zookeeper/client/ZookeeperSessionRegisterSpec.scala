package com.stan.meetapp.zookeeper.client

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.util.Try
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.Assertions._
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.concurrent._
import org.scalatest.mock.MockitoSugar
import org.apache.zookeeper.data.Stat
import org.apache.zookeeper.KeeperException.ConnectionLossException
import org.apache.zookeeper.KeeperException.NodeExistsException


/**
 * @author Stanislav Zhurich
 */
class ZookeeperSessionRegisterSpec extends UnitSpec 
  with ZookeeperSessionRegisterFixture with ScalaFutures{
  
  "ZookeeperSessionRegister " should "register seed" in {
    val f = registerFixture   
    when(f.sessionMock.runForMaster(anyString(), anyString())).thenReturn(Try("testPath"))
    val exec = Executors.newSingleThreadExecutor
    val result = f.zookeeperSessionRegistry.registerSeed(ExecutionContext.fromExecutor(exec))
    whenReady(result) { result =>
      result should be(true)
    }
  }
  
  it should "return false when node already exists" in {
    val f = registerFixture   
    when(f.sessionMock.runForMaster(anyString(), anyString())).thenThrow(classOf[NodeExistsException])
    val exec = Executors.newSingleThreadExecutor
    val result = f.zookeeperSessionRegistry.registerSeed(ExecutionContext.fromExecutor(exec))
    whenReady(result) { result =>
      result should be(false)
    }
  }
  
   it should "return true when was registered but get connection lost" in {
    val f = registerFixture   
    val bytesResp = f.zookeeperSessionRegistry.address.getBytes
    when(f.sessionMock.runForMaster(anyString(), anyString())).thenThrow(classOf[ConnectionLossException])
    when(f.sessionMock.getData("seed")).thenReturn(Try(bytesResp, new Stat()))
    val exec = Executors.newSingleThreadExecutor
    val result = f.zookeeperSessionRegistry.registerSeed(ExecutionContext.fromExecutor(exec))
    whenReady(result) { result =>
      result should be(true)
    }
  }
}