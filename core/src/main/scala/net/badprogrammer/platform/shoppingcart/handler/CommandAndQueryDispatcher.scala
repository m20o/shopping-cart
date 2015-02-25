package net.badprogrammer.platform.shoppingcart.handler

import akka.actor._
import net.badprogrammer.platform.shoppingcart.command.Command
import net.badprogrammer.platform.shoppingcart.handler.CommandAndQueryDispatcher.{Aggregate, View}
import net.badprogrammer.platform.shoppingcart.query.Query

class CommandAndQueryDispatcher(aggregate: Aggregate, views: View*) extends Actor {

  val aggregateActor = aggregate.actorOf(context)
  val viewActors = views.map(target => target.actorOf(context))

  override def receive: Receive = {
    case c: Command => aggregateActor forward c
    case q: Query => {
      for( view <- viewActors) {
        view forward q
      }
    }
  }
}

object CommandAndQueryDispatcher {

  class Target(props: Props, name: String, role:String) {

    val completeName = s"$role-$name"

    def actorOf(context: ActorContext) = context.actorOf(props, completeName)
  }

  case class View(props: Props, name: String) extends Target(props, name, "view")
  case class Aggregate(props: Props, name: String) extends Target(props, name, "aggregate")

  def props(aggregate: Aggregate, views: View*): Props = Props(classOf[CommandAndQueryDispatcher], aggregate, views)

}

