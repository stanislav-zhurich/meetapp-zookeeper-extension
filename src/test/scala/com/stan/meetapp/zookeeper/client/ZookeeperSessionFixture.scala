package com.stan.meetapp.zookeeper.client

import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.data.Stat
import org.mockito.Mockito.mock

trait ZookeeperSessionFixture {
  val host = "test_host"
  val path = "test_path"
  val timeout = 1000
  val watcher: Watcher = null
  val isReadOnly = false
  val data = "test_data"

  def fixture = new {
    val zookeeperMock = mock(classOf[ZooKeeper])
    val zookeeperSession = new ZookeeperSession(host, timeout, watcher, isReadOnly) {
      override lazy val zookeeper = zookeeperMock
    }
    val stat = new Stat
  }
}