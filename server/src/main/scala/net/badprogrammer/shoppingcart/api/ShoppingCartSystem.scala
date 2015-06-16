package net.badprogrammer.shoppingcart.api

import akka.actor.ActorRefFactory
import akka.util.Timeout
import net.badprogrammer.shoppingcart.command.{AddArticle, CartContent, ContentEvent, GetContent}
import net.badprogrammer.shoppingcart.domain.{Article, ShoppingCartId, User}
import net.badprogrammer.shoppingcart.service.Cart.{Created, Exists}
import net.badprogrammer.shoppingcart.service.{ShoppingCartIdGenerator, Cart, ShoppingCarts}
import akka.pattern.ask

import scala.concurrent.{ExecutionContext, Future}

trait ShoppingCartSystem {

  implicit def factory: ActorRefFactory

  implicit def context: ExecutionContext

  implicit def generator: ShoppingCartIdGenerator

  lazy val cartsActor = factory.actorOf(ShoppingCarts.props(RandomArticleHandler.reference))

  def load(id: ShoppingCartId)(implicit timeout: Timeout): Future[CartContent] = (cartsActor ? Cart.Execute(id, GetContent)).mapTo[CartContent]

  def create(caller: Caller)(implicit timeout: Timeout): Future[Option[Created]] = cartsActor ? Cart.Create(User(caller.id, caller.site)) collect {
    case msg: Created => Some(msg)
    case Exists => None
  }

  def addArticleToCart(id: ShoppingCartId, article: Article)(implicit timeout: Timeout): Future[ContentEvent] = (cartsActor ? Cart.Execute(id, AddArticle(article))).mapTo[ContentEvent]
}

object ShoppingCartSystem {

  private class Gateway(val factory: ActorRefFactory, val generator: ShoppingCartIdGenerator, val context: ExecutionContext) extends ShoppingCartSystem

  def apply(factory: ActorRefFactory, generator: ShoppingCartIdGenerator)(implicit context: ExecutionContext): ShoppingCartSystem = new Gateway(factory, generator, context)

}


case class Caller(id: String, site: String)
