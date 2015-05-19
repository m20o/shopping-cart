package net.badprogrammer.platform.shoppingcart

import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, User}
import net.badprogrammer.platform.shoppingcart.util.Sha256

package object service {

  type ShoppingCartIdGenerator = (User) => ShoppingCartId

  implicit object DefaultShoppingCartIdFactory extends ShoppingCartIdGenerator {

    private def generateSha256Hex(user: User) = Sha256(s"${user.id}-${user.context}").asHexString

    override def apply(user: User): ShoppingCartId = ShoppingCartId(generateSha256Hex(user))
  }
}
