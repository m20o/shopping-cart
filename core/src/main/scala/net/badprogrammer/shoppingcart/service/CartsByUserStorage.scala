package net.badprogrammer.shoppingcart.service

import net.badprogrammer.shoppingcart.domain.{ShoppingCartId, User}

trait CartsByUserStorage {

  def insert(user: User, id: ShoppingCartId)

  def findByUser(user: User): Option[ShoppingCartId]

}
