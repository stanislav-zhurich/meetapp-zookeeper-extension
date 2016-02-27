package com.stan.meetapp.zookeeper.client

import org.scalatest.Assertions._
import org.scalatest.FlatSpec
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.mock.MockitoSugar
import org.apache.zookeeper.ZooKeeper
import org.scalatest.BeforeAndAfterEach
import org.apache.zookeeper.Watcher
import org.mockito.Matchers._
import org.apache.zookeeper.data.Stat
import org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE
import org.apache.zookeeper.CreateMode
import scala.util.Try
import org.apache.zookeeper.AsyncCallback.StringCallback

/**
 * @author Stanislav Zhurich
 */
class ZookeeperSessionTest extends UnitSpec with ZookeeperSessionFixture{
  
  
  "Zookeeper " should "be called close method" in {
     val f = fixture
     doNothing().when(f.zookeeperMock).close()   
     f.zookeeperSession.close()
     verify(f.zookeeperMock, times(1)).close()
  
  }
  
  it should "return data when exists" in {
    val f = fixture
    when(f.zookeeperMock.getData(anyString(), anyBoolean(), any())).thenReturn(data.getBytes)
    import com.stan.meetapp.zookeeper.client.ZookeeperSessionRegister.bytesToString
    val result:Try[Tuple2[String, Stat]] = f.zookeeperSession.getData(path)
    assert(result.get._1 == data)
  }
  
  it should "return error when exception" in {
    val f = fixture
    when(f.zookeeperMock.getData(anyString(), anyBoolean(), any())).thenThrow(classOf[Exception]);
    
    import com.stan.meetapp.zookeeper.client.ZookeeperSessionRegister.bytesToString
    val result:Try[Tuple2[String, Stat]] = f.zookeeperSession.getData(path)
    assert(result.isFailure)
  }
  
  it should "create master znode" in {
     val f = fixture
     when(f.zookeeperMock.create(path, data.getBytes, OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL)).thenReturn(path)
     val result = f.zookeeperSession.runForMaster(path, data)
     assert(result.get == path)
  }
  
 
  it should "failed when create master node" in {
     val f = fixture
     when(f.zookeeperMock.create(path, data.getBytes, OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL)).thenThrow(classOf[Exception]);
     val result = f.zookeeperSession.runForMaster(path, data)
     assert(result.isFailure)
  }
  
}