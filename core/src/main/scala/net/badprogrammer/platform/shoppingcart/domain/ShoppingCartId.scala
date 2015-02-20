package net.badprogrammer.platform.shoppingcart.domain

import net.badprogrammer.platform.shoppingcart.command.Create
import net.badprogrammer.platform.shoppingcart.util.Sha256

case class ShoppingCartId(value: String)

object ShoppingCartId {

  type Factory = (Create) => String

  val defaultFactory: Factory = c => Sha256(s"${c.businessId}-${c.userId}").asHexString

  def apply(v1: Create, f: Factory = defaultFactory): ShoppingCartId = ShoppingCartId(f(v1))

}