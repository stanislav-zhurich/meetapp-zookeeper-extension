package com.stan.meetapp.zookeeper.client

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.util.Try
import org.apache.zookeeper.KeeperException.ConnectionLossException
import org.apache.zookeeper.KeeperException.NodeExistsException
import org.apache.zookeeper.data.Stat
import org.mockito.Matchers.anyString
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.Seconds
import org.scalatest.time.Span

/**
 * @author Stanislav Zhurich
 */
class ZookeeperSessionRegisterSpec extends UnitSpec
    with ZookeeperSessionRegisterFixture with ScalaFutures {

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
    val bytesResp = f.seedPath.getBytes
    when(f.sessionMock.runForMaster(anyString(), anyString())).thenThrow(classOf[ConnectionLossException])
    when(f.sessionMock.getData("/seed")).thenReturn(Try(bytesResp, new Stat()))
    val exec = Executors.newSingleThreadExecutor
    val result = f.zookeeperSessionRegistry.registerSeed(ExecutionContext.fromExecutor(exec))
   
    
    whenReady (result, timeout(Span(600, Seconds))) { r =>
      r should be(true)
    }
  }

  it should "return data" in {
    val f = registerFixture
    val bytesResp = f.seedPath.getBytes
    when(f.sessionMock.getData("/seed")).thenReturn(Try(bytesResp, new Stat()))
    val exec = Executors.newSingleThreadExecutor
    val result = f.zookeeperSessionRegistry.getSeed(ExecutionContext.fromExecutor(exec))
    whenReady(result, timeout(Span(600, Seconds))) { result =>
      result should be(Seed(f.seedPath))
    }
  }
}