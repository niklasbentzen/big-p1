package model

import scala.collection.mutable

class Network(val ringSize: Int) {
  private val servers: mutable.TreeMap[Int, Server] = mutable.TreeMap()

  def addServer(serverId: Int): Server = {
    if (servers.contains(serverId)) {
      throw new RuntimeException(s"Server $serverId already exists!")
    }

    val newServer = new Server(serverId, ringSize)
    servers(serverId) = newServer

    // updateSuccessorPredecessor()

    println(s"Server $serverId added to the network.")
    newServer
  }

  def findServer(serverId: Int): Option[Server] = servers.get(serverId)

  def displayNetwork(): Unit = {
    println("\nCurrent Network State:")
    servers.values.foreach { server =>
      println(s"Server ${server.id} -> Successor: ${server.successor.map(_.id).getOrElse("None")}, Predecessor: ${server.predecessor.map(_.id).getOrElse("None")}")
    }
  }
}