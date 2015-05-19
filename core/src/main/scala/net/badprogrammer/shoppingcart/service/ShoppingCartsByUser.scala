package net.badprogrammer.shoppingcart.service

import akka.actor.Props
import akka.persistence.PersistentView
import Cart.{Created, FindByUser}

import scala.concurrent.duration._
import scala.language.postfixOps

class ShoppingCartsByUser(storage: CartsByUserStorage) extends PersistentView {

  val viewId: String = ShoppingCartsByUser.ID

  val persistenceId: String = ShoppingCarts.ID

  def receive: Receive = {
    case Created(user, id) => storage.insert(user, id)
    case FindByUser(user) => sender() ! storage.findByUser(user)
  }

  override val autoUpdateInterval = 10 millis
}

object ShoppingCartsByUser {

  val ID = s"${ShoppingCarts.ID}ByUser"

  def props(storage: CartsByUserStorage) = Props(classOf[ShoppingCartsByUser], storage)

}
