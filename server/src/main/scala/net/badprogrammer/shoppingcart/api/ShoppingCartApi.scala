package net.badprogrammer.shoppingcart.api

import akka.pattern.ask
import akka.util.Timeout
import net.badprogrammer.shoppingcart.command.{CartContent, GetContent, ArticleAdded, AddArticle}
import net.badprogrammer.shoppingcart.domain.{ShoppingCartId, Article, User}
import net.badprogrammer.shoppingcart.service.{ShoppingCartIdGenerator, ShoppingCarts, Cart}
import Cart.{Created, Execute, Exists}
import spray.http.{HttpResponse, StatusCodes}
import spray.routing.Route

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

trait ShoppingCartApi extends ShoppingCartApplication with ShoppingCartProtocols {

  implicit def idGenerator: ShoppingCartIdGenerator

  implicit val context = ExecutionContext.Implicits.global

  def carts = actorRefFactory.actorOf(ShoppingCarts.props(RandomArticleHandler.reference))

  lazy val service = new ShoppingCartRepository(carts)

  def routes: Route = CreateShoppingCart ~ AddArticleToCart

  def GetShoppingCart = pathPrefix("carts" / IntNumber) {
    s =>
      get {
        implicit val timeout = Timeout(1 second)
        complete {
          (carts ? Execute(ShoppingCartId(s.toString), GetContent)).mapTo[CartContent].map(CartAdapter.apply)
        }
      }
  }

  def CreateShoppingCart = path("carts") {
    post {
      entity(as[Caller]) { caller => ctx =>
        implicit val timeout = Timeout(1 second)
        val a = carts ? Cart.Create(User(caller.id, caller.site)) collect {
          case msg: Created => ctx.complete {
            HttpResponse(
              status = StatusCodes.Created,
              entity = msg.id.value
            )
          }
          case Exists => ctx.complete {
            HttpResponse(status = StatusCodes.Conflict)
          }
        }
      }
    }
  }

  def AddArticleToCart = path("carts" / Segment / "articles") { s =>
    val id = ShoppingCartId(s)
    println(">>>> " + id)
    put {
      entity(as[String]) { suca => ctx =>
        val articleId = Article(suca)
        println(">>>> " + articleId)

        implicit val timeout = Timeout(1 second)
        (carts ? Cart.Execute(id, AddArticle(articleId))).collect {
          case a: ArticleAdded => ctx.complete {
            CartItem(id = a.article.id, description = "cazzi", quantity = a.quantity)
          }
          case e => println(">>>> " + e)
        }
      }
    }
  }
}
