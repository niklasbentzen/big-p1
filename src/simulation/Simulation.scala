package simulation

import model._
import scala.util.Random
import java.util.UUID.randomUUID

object Simulation {
  val m = 160
  val ringSize: BigInt = BigInt(2).pow(m)

  def main(args: Array[String]): Unit = {
    val network = new Network(m)
    val nodes = 10
    val extents = 10000
    // val replicationFactor = 3
    val workload = 100000

    // Create initial nodes
    (1 to nodes).foreach(_ => network.addNode((randomUUID().toString)))

    // Assign data and simulate workload
    for (i <- 0 until extents) {
      val filename = randomFileName()
      val key = Hash.hash(filename, ringSize)
      network.nodes.head._2.put(key, filename)
    }

    // Print load distribution
    for (node <- network.nodes) {
      println(s"${node._2.data.size}")
    }
  }

  private def randomFileName(length: Int = 10): String = {
    Random.alphanumeric.take(length).mkString
  }
}
