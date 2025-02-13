package model

object Main {
  def main(args: Array[String]): Unit = {

    val n = new Network(8)
    for (i <- 0 to 10) {
      n.addNode(s"192.168.1.$i")
    }
    //n.displayNetwork()


  }
}