package model

import java.security.MessageDigest

object Hash {
  def hash(identifier: String, ringSize: BigInt): BigInt = {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(identifier.getBytes)
    BigInt(1, bytes) % ringSize
  }
}