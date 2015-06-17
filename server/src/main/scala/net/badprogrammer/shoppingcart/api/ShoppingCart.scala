package net.badprogrammer.shoppingcart.api

import net.badprogrammer.shoppingcart.command.CartContent

case class CartItem(id: String, description: String, quantity: Int)

case class ShoppingCart(items: List[CartItem]) {

  def item(id: String): Option[CartItem] = items.find(_.id == id)

}

object CartAdapter {

  def apply(content: CartContent): ShoppingCart = ShoppingCart(content.items.map(i => CartItem(i.article.id, i.article.id, i.quantity)))

}
