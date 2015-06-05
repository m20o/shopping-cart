package net.badprogrammer.shoppingcart.api

import akka.pattern.ask
import akka.util.Timeout
import net.badprogrammer.shoppingcart.command._
import net.badprogrammer.shoppingcart.domain.{Article, ShoppingCartId, User}
import net.badprogrammer.shoppingcart.service.Cart.{Created, Execute, Exists}
import net.badprogrammer.shoppingcart.service.{Cart, ShoppingCartIdGenerator, ShoppingCarts}
import spray.http.{HttpResponse, StatusCodes}
import spray.routing.Route

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

trait ShoppingCartApi extends ShoppingCartApplication with ShoppingCartProtocols {

  implicit def idGenerator: ShoppingCartIdGenerator

  implicit val context = ExecutionContext.Implicits.global

  def carts = actorRefFactory.actorOf(ShoppingCarts.props(RandomArticleHandler.reference))

  lazy val service = new ActorSystemGateway(carts, context)

  def routes: Route = CreateShoppingCart1 ~ AddArticleToCart ~ RetriveCartItem

  implicit val timeout = Timeout(3 second)

  def GetShoppingCart = pathPrefix("carts" / IntNumber) {
    s =>
      get {
        complete {
          (carts ? Execute(ShoppingCartId(s.toString), GetContent)).mapTo[CartContent].map(CartAdapter.apply)
        }
      }
  }

  def CreateShoppingCart1 = path("carts") {
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

  def CreateShoppingCart = path("carts") {
    post {
      entity(as[Caller]) { caller => ctx =>
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
    put {
      entity(as[String]) { articleId => ctx =>
        val article = Article(articleId)
        (carts ? Cart.Execute(id, AddArticle(article))) collect {
          case msg: ArticleAdded => ctx.complete(CartItem(msg.article.id, "food", msg.quantity))
        }
      }
    }
  }

  def RetriveCartItem = path("carts" / Segment / "articles" / Segment) { (cartId, articleId) =>
    val id = ShoppingCartId(cartId)
    get { ctx =>
      (carts ? Cart.Execute(id, GetContent)) collect {
        case content: CartContent => ctx.complete(
          content.items
            .find(_.article.id == articleId)
            .map(a => CartItem(a.article.id, a.article.id, a.quantity))
        )
      }
    }
  }

}
