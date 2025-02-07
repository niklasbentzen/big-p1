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

    updateSuccessorPredecessor()

    println(s"Server $serverId added to the network.")
    newServer
  }

  /** Find a server by ID */
  def findServer(serverId: Int): Option[Server] = servers.get(serverId)

  /** Update successor & predecessor for all servers */
  private def updateSuccessorPredecessor(): Unit = {
    val serverIds = servers.keys.toSeq.sorted
    if (serverIds.isEmpty) return

    for (i <- serverIds.indices) {
      val current = servers(serverIds(i))
      val successor = servers.get(serverIds((i + 1) % serverIds.length))
      val predecessor = servers.get(serverIds((i - 1 + serverIds.length) % serverIds.length))

      current.successor = successor
      current.predecessor = predecessor

      println(s"Server ${current.id} -> Successor: ${successor.map(_.id).getOrElse("None")}, Predecessor: ${predecessor.map(_.id).getOrElse("None")}")
    }
  }

  /** Display network state */
  def displayNetwork(): Unit = {
    println("\nCurrent Network State:")
    servers.values.foreach { server =>
      println(s"Server ${server.id} -> Successor: ${server.successor.map(_.id).getOrElse("None")}, Predecessor: ${server.predecessor.map(_.id).getOrElse("None")}")
    }
  }
}