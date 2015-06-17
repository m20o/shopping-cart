package net.badprogrammer.shoppingcart.api

import akka.util.Timeout
import net.badprogrammer.shoppingcart.command._
import net.badprogrammer.shoppingcart.domain.{Article, ShoppingCartId}
import net.badprogrammer.shoppingcart.service.ShoppingCartIdGenerator
import spray.http.{HttpResponse, StatusCodes}
import spray.routing.Route

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

trait ShoppingCartApi extends ShoppingCartApplication with ShoppingCartProtocols {

  def idGenerator: ShoppingCartIdGenerator

  implicit def context = ExecutionContext.Implicits.global
  implicit val timeout = Timeout(3 second)

  lazy val service = ShoppingCartSystem(actorRefFactory, idGenerator)

  def routes: Route = CreateCart ~ PutArticleIntoCart ~ GetCartItem ~ GetCartContent

  def CreateCart = path("carts") {
    post {
      entity(as[Caller]) { caller =>
        complete {
          service.create(caller).collect {
            case Some(msg) => HttpResponse(
              status = StatusCodes.Created,
              entity = msg.id.value
            )
            case None => HttpResponse(status = StatusCodes.Conflict)
          }
        }
      }
    }
  }

  def PutArticleIntoCart = path("carts" / Segment / "articles") { s =>
    val id = ShoppingCartId(s)
    put {
      entity(as[String]) { articleId => ctx =>
        val article = Article(articleId)
        service.addArticleToCart(id, article).collect {
          case msg: ArticleAdded => ctx.complete(CartItem(msg.article.id, "food", msg.quantity))
        }
      }
    }
  }

  def GetCartItem = path("carts" / Segment / "articles" / Segment) { (cartId, articleId) =>
    val id = ShoppingCartId(cartId)
    get {
      complete {
        service.load(id).map(CartAdapter.apply).map(_.item(articleId))
      }
    }

  }

  def GetCartContent = path("carts" / Segment) { cartId =>
    val id = ShoppingCartId(cartId)
    get {
      complete {
        service.load(id).map(CartAdapter.apply)
      }
    }
  }
}
