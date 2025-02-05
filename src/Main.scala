object Main {
  def main(args: Array[String]): Unit = {
    val n1 = new Server(1)
    val n2 = new Server(2)
    n1.put("a", "1")
    n1.put("b", "2")
    println("Get 'b': " + n1.get("b"))
    n2.put("c", "3")
    n1.displayData()
    n2.displayData()
  }
}