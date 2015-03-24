package net.badprogrammer.shoppingcart.api

import akka.actor.ActorRef

trait ShoppingCartServiceProvider {

  def shoppingCartSystem: ShoppingCartSystem

}
