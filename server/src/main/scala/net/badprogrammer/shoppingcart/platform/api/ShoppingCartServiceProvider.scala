package net.badprogrammer.shoppingcart.platform.api

import akka.actor.ActorRef

trait ShoppingCartServiceProvider {

  def shoppingCartSystem: ActorRef

}
