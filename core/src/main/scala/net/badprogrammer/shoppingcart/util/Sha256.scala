package net.badprogrammer.shoppingcart.util

import java.nio.charset.Charset
import java.security.MessageDigest

case class Sha256(bytes: Array[Byte]) {
  val asHexString: String = BigInt(bytes).abs.formatted("%64x").toUpperCase
}

object Sha256 {

  private val UTF8 = Charset.forName("UTF-8")

  def apply(text: String): Sha256 = Sha256(compute(text.getBytes(UTF8)))

  private def compute(bytes: Array[Byte]) = {
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(bytes)
    digest.digest()
  }
}
