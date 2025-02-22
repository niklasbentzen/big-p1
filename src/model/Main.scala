package model

import scala.collection.mutable

object Main {
  def main(args: Array[String]): Unit = {

    val network = new Network(m = 160, replicationFactor = 3, vnodesPerPhysicalNode = 3)

    val node1 = network.addPhysicalNode("Node1")
    val node2 = network.addPhysicalNode("Node2")


    node1.put("hello", "hello")
    node1.put("hell1o", "hell2o")
    node2.put("122","12")
    node2.put("1222","122")

    val physicalDataCount = mutable.Map[PhysicalNode, Int]().withDefaultValue(0)

    // Count data from each virtual node and sum it per physical node
    for ((_, vnode) <- network.virtualNodes) {
      physicalDataCount(vnode.physicalNode) += vnode.data.size
    }

    // Print the data distribution
    for ((pNode, totalData) <- physicalDataCount) {
      println(s"${pNode.identifier} -> $totalData entries")
    }
  }
}