package model

import scala.collection.mutable

class PhysicalNode(val identifier: String, val numVNodes: Int, val ringSize: BigInt, replicationFactor: Int) {
  val vnodes: Seq[VirtualNode] = (0 until numVNodes).map { i =>
    val vnodeId = Hash.hash(identifier + s"_$i", ringSize) // Generate vnode IDs
    new VirtualNode(this, vnodeId, ringSize, replicationFactor)
  }

  def join(existingVNode: Option[VirtualNode]): Unit = {
    vnodes.foreach(_.join(existingVNode))
  }

  def put(key: String, value: String): Unit = {
    val keyHash = Hash.hash(key, ringSize)
    val vnode = getResponsibleVNode(keyHash)
    vnode.put(keyHash, value)
  }

  def get(key: String): Option[String] = {
    val keyHash = Hash.hash(key, ringSize)
    val vnode = getResponsibleVNode(keyHash)
    vnode.get(key)
  }

  private def getResponsibleVNode(key: BigInt): VirtualNode = {
    vnodes.minBy(vnode => (key - vnode.vnodeId).abs)
  }

  def displayAllData(): Unit = {
    vnodes.foreach(_.displayData())
  }
}