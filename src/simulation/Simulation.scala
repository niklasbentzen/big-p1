package simulation

import model._
import scala.util.Random
import java.util.UUID.randomUUID

object Simulation {
  val m = 120
  val ringSize: BigInt = BigInt(2).pow(m)

  def main(args: Array[String]): Unit = {
    val nodes = 5
    val extents = 10000
    val replicationFactor = 3
    // val workload = 100000

    val network = new Network(m, replicationFactor)



    // Create initial nodes
    (1 to nodes).foreach { i =>
      val key = Hash.hash(randomUUID().toString, ringSize).toString()
      network.addNode(key)
    }

    dataToRandomNode(extents, network)

    // Print load distribution
    for (node <- network.nodes) {
      println(s"${node._2.data.size}")
    }
  }

  private def dataToRandomNode(extents: Int, network: Network): Unit = {
    // Assign data to random node
    for (_ <- 0 until extents) {
      val filename = randomFileName()
      val key = Hash.hash(filename, ringSize)

      // Pick a random node from the network
      val randomNode = network.nodes.toSeq(Random.nextInt(network.nodes.size))._2
      randomNode.put(key, filename)
    }
  }

  private def dataToFirstNode(extents: Int, network: Network): Unit = {
    // Assign data to first node
    for (i <- 0 until extents) {
      val filename = randomFileName()
      val key = Hash.hash(filename, ringSize)
      network.nodes.head._2.put(key, filename)
    }
  }

  private def randomFileName(length: Int = 10): String = {
    Random.alphanumeric.take(length).mkString
  }
}