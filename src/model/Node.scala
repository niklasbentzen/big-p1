package model

import scala.collection.mutable

class Node(val identifier: String, val ringSize: BigInt) {
  val id: BigInt = Hash.hash(identifier, ringSize)

  var successor: Option[Node] = None
  var predecessor: Option[Node] = None
  private val fingerTable: mutable.Map[Int, Node] = mutable.Map()

  val data: mutable.Map[BigInt, String] = mutable.Map()

  def put(key: BigInt, value: String): Unit = {
    if (isResponsibleForKey(key)) {
      data(key) = value
    } else {
      routeToSuccessor(key, value)
    }
  }

  def get(key: String): Option[String] = {
    val keyHash = Hash.hash(key, ringSize)
    if (data.contains(keyHash)) Some(data(keyHash))
    else successor.flatMap(_.get(key))
  }

  private def isResponsibleForKey(key: BigInt): Boolean = {
    val predId = predecessor.map(_.id).getOrElse(id - 1)
    (key > predId && key <= id) || (predId > id && (key > predId || key <= id))
  }

  private def routeToSuccessor(key: BigInt, value: String): Unit = {
    findSuccessor(key).put(key, value)
  }

  def join(existingNode: Option[Node]): Unit = {
    existingNode match {
      case None =>
        successor = Some(this)
        predecessor = Some(this)
        //println(s"Server $id initialized as the first server in the Chord ring.")

      case Some(server) =>
        val newSuccessor = server.findSuccessor(this.id)
        this.successor = Some(newSuccessor)
        this.predecessor = newSuccessor.predecessor

        // Update existing node links
        newSuccessor.predecessor = Some(this)
        this.predecessor.foreach(_.successor = Some(this))

        // Take over some data from the successor
        migrateData(newSuccessor)

        //println(s"Node $id joined Chord ring. Successor: ${successor}, Predecessor: ${this.predecessor.map(_.id)}")
    }
  }

  private def findSuccessor(id: BigInt): Node = {
    if (this.successor.isEmpty || this.successor.get == this) {
      return this
    }
    if ((this.id < id && id <= this.successor.get.id) ||
      (this.id > this.successor.get.id && (id > this.id || id <= this.successor.get.id))) {
      return this.successor.get
    }
    return this.successor.get.findSuccessor(id)
  }

  private def migrateData(fromNode: Node): Unit = {
    val keysToTake = fromNode.data.keys.filter(key => key <= this.id)
    keysToTake.foreach { key =>
      this.data(key) = fromNode.data(key)
      fromNode.data.remove(key)
    }
  }

  def displayData(): Unit = println(s"Server $id Data: $data")

  def displayFingerTable(): Unit = {
    println(s"Finger table for server $id:")
    fingerTable.foreach { case (index, server) =>
      println(s"Index $index -> Server ${server.id}")
    }
  }
}