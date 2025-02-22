package simulation

import model._

import scala.util.Random
import java.util.UUID.randomUUID
import scala.collection.mutable

object Simulation {
  val m = 120
  val ringSize: BigInt = BigInt(2).pow(m)

  def main(args: Array[String]): Unit = {
    val nodes = 100
    val extents = 10000
    val replicationFactor = 3
    val vnodesPerPhysicalNode = 10
    // val workload = 100000

    val network = new Network(m, replicationFactor, vnodesPerPhysicalNode)

    // Create initial nodes
    (1 to nodes).foreach { i =>
      val key = Hash.hash(randomUUID().toString, ringSize).toString()
      network.addPhysicalNode(key)
    }

    dataToFirstNode(extents, network)

    val physicalDataCount = mutable.Map[PhysicalNode, Int]().withDefaultValue(0)

    // Count data from each virtual node and sum it per physical node
    for ((_, vnode) <- network.virtualNodes) {
      physicalDataCount(vnode.physicalNode) += vnode.data.size
    }

    // Print the data distribution
    for ((pNode, totalData) <- physicalDataCount) {
      println(s"$totalData")
    }
  }

  private def dataToFirstNode(extents: Int, network: Network): Unit = {
    // Assign data to first node
    for (i <- 0 until extents) {
      val filename = randomFileName()
      val key = Hash.hash(filename, ringSize)
      network.virtualNodes.head._2.put(key, filename)
    }
  }

  private def randomFileName(length: Int = 10): String = {
    Random.alphanumeric.take(length).mkString
  }
}