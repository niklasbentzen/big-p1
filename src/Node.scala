import scala.collection.mutable

class Node(val id: Int) {
  private val data = mutable.Map[String, String]()

  def put(key: String, value: String): Unit = {
    data(key) = value
    println(s"Node $id stored: ($key -> $value)")
  }

  def get(key: String): Option[String] =
    data.get(key)

  def remove(key: String): Unit =
    data.remove(key)

  def displayData(): Unit =
    println(s"Node $id Data: $data")
}