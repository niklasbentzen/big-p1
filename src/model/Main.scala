package model

object Main {
  def main(args: Array[String]): Unit = {
    val n = new Network(10)
    n.addServer(1)
    n.addServer(2)
    n.addServer(5)
    n.displayNetwork()

    n.findServer(1) match {
      case Some(server) => server.put(File(3, "hello"))
    }

    n.findServer(5) match {
      case Some(server) => {
        println(server.get(3))
        server.displayFingerTable()
      }
    }


  }
}