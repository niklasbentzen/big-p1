package model

import scala.collection.mutable

class Network(val m: Int) {
  val ringSize: BigInt = BigInt(2).pow(m)
  val nodes: mutable.TreeMap[String, Node] = mutable.TreeMap()

  /** Add a new server to the Chord ring */
  def addNode(nodeId: String): Node = {
    if (nodes.contains(nodeId)) {
      throw new RuntimeException(s"Server $nodeId already exists!")
    }

    val newServer = new Node(nodeId, ringSize)
    val existingServer = nodes.headOption.map(_._2) // Pick any existing server for reference
    newServer.join(existingServer)

    nodes(nodeId) = newServer
    newServer
  }

  /** Display the Chord network state */
  def displayNetwork(): Unit = {
    println("\nCurrent Network State:")
    nodes.values.foreach { server =>
      println(s"Server ${server.id} -> Successor: ${server.successor.map(_.id).getOrElse("None")}, Predecessor: ${server.predecessor.map(_.id).getOrElse("None")}")
    }
  }
}