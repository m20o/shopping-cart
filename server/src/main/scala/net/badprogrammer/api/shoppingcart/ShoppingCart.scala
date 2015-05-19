package net.badprogrammer.api.shoppingcart

import net.badprogrammer.platform.shoppingcart.command.CartContent

case class CartItem(id: String, description: String, quantity: Int)

case class ShoppingCart(items: List[CartItem]) {

  def apply(content: CartContent): ShoppingCart = ShoppingCart(content.items.map(i => CartItem(i.article.id, i.article.id, i.quantity)))

}

object CartAdapter {

  def apply(content: CartContent): ShoppingCart = ShoppingCart(content.items.map(i => CartItem(i.article.id, i.article.id, i.quantity)))

}
