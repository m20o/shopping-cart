package net.badprogrammer.platform.shoppingcart.service

import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, User}

trait CartsByUserStorage {

  def insert(user: User, id: ShoppingCartId)

  def findByUser(user: User): Option[ShoppingCartId]

}
