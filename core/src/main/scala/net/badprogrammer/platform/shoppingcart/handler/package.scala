package net.badprogrammer.platform.shoppingcart

import akka.actor.{ActorContext, ActorRef}

package object handler {

  type LocalFactory = (ActorContext) => ActorRef

}
