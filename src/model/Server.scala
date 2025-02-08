package model

import scala.collection.mutable

class Server(val id: Int, ringSize: Int) {
  private val data: mutable.Map[Int, File]= mutable.Map()
  var successor: Option[Server] = None
  var predecessor: Option[Server] = None
  val fingerTable: mutable.Map[Int, Server] = mutable.Map()

  def put(file: File): Unit = {
    val key = hash(file.id)
    if (isResponsibleForKey(key)) {
      data(key) = file
      println(s"Server $id stored: ($key -> ${file.value})")
    } else {
      routeToSuccessor(key, file)
    }
  }

  private def isResponsibleForKey(key: Int): Boolean = {
    val predId = predecessor.map(_.id).getOrElse(id - 1)
    (key > predId && key <= id) || (predId > id && (key > predId || key <= id))
  }

  private def routeToSuccessor(key: Int, file: File): Unit = {
    findSuccessor(key).put(file)
  }

  private def findSuccessor(key: Int): Server = {
    if (isResponsibleForKey(key)) this
    else fingerTable.find { case (_, server) => server.id >= key }.map(_._2)
      .orElse(successor)
      .getOrElse(this)
  }

  def get(key: Int): Option[File] = {
    if (isResponsibleForKey(key)) {
      data.get(key: Int)
    } else {
      println("Didn't find key here, looking at next server...")
      findSuccessor(key).get(key)
    }
  }

  def remove(key: Int): Unit = data.remove(key: Int)

  def displayData(): Unit = println(s"Server $id Data: $data")

  def displayFingerTable(): Unit = {
    println(s"Finger table for server $id:")
    fingerTable.foreach { case (index, server) =>
      println(s"Index $index -> Server ${server.id}")
    }
  }

  private def hash(value: Int): Int = {
    value % ringSize
  }
}

