package model

import scala.collection.mutable

class VirtualNode(val physicalNode: PhysicalNode, val vnodeId: BigInt, val ringSize: BigInt, replicationFactor: Int) {
  var successor: Option[VirtualNode] = None
  var predecessor: Option[VirtualNode] = None
  private val fingerTable: mutable.Map[Int, VirtualNode] = mutable.Map()
  val data: mutable.Map[BigInt, String] = mutable.Map()

  def put(key: BigInt, value: String): Unit = {
    if (isResponsibleForKey(key)) {
      data(key) = value
    } else {
      routeToSuccessor(key, value)
    }

    // Replication across `replicationFactor` successors
    var node = findSuccessor(key)
    for (_ <- 1 to replicationFactor) {
      node.data(key) = value
      node = node.successor.getOrElse(node)
    }
  }

  def get(key: String): Option[String] = {
    val keyHash = Hash.hash(key, ringSize)
    if (data.contains(keyHash)) Some(data(keyHash))
    else successor.flatMap(_.get(key))
  }

  private def isResponsibleForKey(key: BigInt): Boolean = {
    val predId = predecessor.map(_.vnodeId).getOrElse(vnodeId - 1)
    (key > predId && key <= vnodeId) || (predId > vnodeId && (key > predId || key <= vnodeId))
  }

  private def routeToSuccessor(key: BigInt, value: String): Unit = {
    findSuccessor(key).put(key, value)
  }

  def join(existingNode: Option[VirtualNode]): Unit = {
    existingNode match {
      case None =>
        successor = Some(this)
        predecessor = Some(this)

      case Some(server) =>
        val newSuccessor = server.findSuccessor(this.vnodeId)
        this.successor = Some(newSuccessor)
        this.predecessor = newSuccessor.predecessor

        newSuccessor.predecessor = Some(this)
        this.predecessor.foreach(_.successor = Some(this))

        migrateData(newSuccessor)
    }
  }

  private def findSuccessor(id: BigInt): VirtualNode = {
    if (this.successor.isEmpty || this.successor.get == this) {
      return this
    }
    if ((this.vnodeId < id && id <= this.successor.get.vnodeId) ||
      (this.vnodeId > this.successor.get.vnodeId && (id > this.vnodeId || id <= this.successor.get.vnodeId))) {
      return this.successor.get
    }
    return this.successor.get.findSuccessor(id)
  }

  private def migrateData(fromNode: VirtualNode): Unit = {
    val keysToTake = fromNode.data.keys.filter(key => key <= this.vnodeId)
    keysToTake.foreach { key =>
      this.data(key) = fromNode.data(key)
      fromNode.data.remove(key)
    }
  }

  def displayData(): Unit = println(s"VNode $vnodeId Data: $data")

  def displayFingerTable(): Unit = {
    println(s"Finger table for vnode $vnodeId:")
    fingerTable.foreach { case (index, server) =>
      println(s"Index $index -> VNode ${server.vnodeId}")
    }
  }
}