package model

import java.security.MessageDigest

object Hash {
  val method = 256

  def hash(key: String, ringSize: BigInt): BigInt = {
    method match {
      case 1 => sha1(key: String, ringSize: BigInt)
      case 256 => sha256(key: String, ringSize: BigInt)
    }
  }

  def sha1(key: String, ringSize: BigInt): BigInt = {
    val sha1 = MessageDigest.getInstance("SHA-1")
    val hashBytes = sha1.digest(key.getBytes)
    val hashInt = BigInt(1, hashBytes) // Convert to positive BigInt
    hashInt % ringSize // Ensure it's within [0, 2^m - 1]
  }

  def sha256(key: String, ringSize: BigInt): BigInt = {
    val sha256 = MessageDigest.getInstance("SHA-256")
    val hashBytes = sha256.digest(key.getBytes)
    val hashInt = BigInt(1, hashBytes) // Convert to positive BigInt
    hashInt % ringSize // Ensure it's within [0, 2^m - 1]
  }
}
