package net.badprogrammer.platform.shoppingcart.handler

import akka.actor._
import net.badprogrammer.platform.shoppingcart.command.Command
import net.badprogrammer.platform.shoppingcart.query.Query

class CommandAndQueryDispatcher(aggregateFactory: LocalFactory, viewFactor: LocalFactory) extends Actor {

  val aggregate = aggregateFactory(context)

  val view = viewFactor(context)

  override def receive: Receive = {
    case x: Command => aggregate forward x
    case q: Query => view forward q
  }
}

