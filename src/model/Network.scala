package model

import scala.collection.mutable

class Network(val m: Int, val replicationFactor: Int, val vnodesPerPhysicalNode: Int) {
  val ringSize: BigInt = BigInt(2).pow(m)

  val physicalNodes: mutable.TreeMap[String, PhysicalNode] = mutable.TreeMap()

  val virtualNodes: mutable.TreeMap[BigInt, VirtualNode] = mutable.TreeMap()

  def addPhysicalNode(nodeId: String): PhysicalNode = {
    if (physicalNodes.contains(nodeId)) {
      throw new RuntimeException(s"Physical Node $nodeId already exists!")
    }

    val newPhysicalNode = new PhysicalNode(nodeId, vnodesPerPhysicalNode, ringSize, replicationFactor)
    physicalNodes(nodeId) = newPhysicalNode

    newPhysicalNode.vnodes.foreach { vnode =>
      val maybeVNode = virtualNodes.headOption.map(_._2) // Pick any existing vnode
      vnode.join(maybeVNode)
      virtualNodes(vnode.vnodeId) = vnode
    }

    newPhysicalNode
  }

  def displayNetwork(): Unit = {
    println("Current Network State:")

    println("Physical Nodes")
    physicalNodes.values.foreach { pNode =>
      println(s"Physical Node ${pNode.identifier} with ${pNode.vnodes.size} vnodes")
    }

    println("Virtual Nodes")
    virtualNodes.values.foreach { vnode =>
      println(s"VNode ${vnode.vnodeId} -> Successor: ${vnode.successor.map(_.vnodeId).getOrElse("None")}, Predecessor: ${vnode.predecessor.map(_.vnodeId).getOrElse("None")}")
    }
  }
}