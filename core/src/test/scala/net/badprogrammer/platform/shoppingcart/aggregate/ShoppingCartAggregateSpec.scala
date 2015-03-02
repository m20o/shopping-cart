package net.badprogrammer.platform.shoppingcart.aggregate

import akka.actor._
import net.badprogrammer.platform.shoppingcart.TestingFixture
import net.badprogrammer.platform.shoppingcart.command._
import net.badprogrammer.platform.shoppingcart.domain.{Item, Article, ShoppingCartId}
import net.badprogrammer.platform.shoppingcart.query._
import net.badprogrammer.platform.shoppingcart.testsupport.PersistentActorSpec
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.duration._
import scala.language.implicitConversions


class ShoppingCartAggregateSpec extends PersistentActorSpec with BeforeAndAfterEach {

  var cart: ActorRef = _

  override protected def beforeEach(): Unit = {
    cart = createCartActorWithId(nextId("test-cart"))
  }

  def createCartActorWithId(id: String): ActorRef = {
    system.actorOf(ShoppingCartAggregate.props(ShoppingCartId(id), TestingFixture.FakeArticleRepository(system)), id)
  }

  override protected def afterEach(): Unit = {
    cart ! PoisonPill
  }

  implicit def articlesToItemList(v: (Article, Int)): List[Item] = List(Item(v._1, v._2))

  import net.badprogrammer.platform.shoppingcart.TestingFixture._

  "A shopping cart" must {

    "add a product" in {

      cart ! AddArticle(Flight)

      expectMsg(ArticleAdded(Flight, 1))

    }

    "refuse to add a product when it's not available" in {

      cart ! AddArticle(Cocaine)

      expectMsg(ArticleNotAvailable(Cocaine, 1, "We cannot sell drugs"))

    }

    "add a product with a quantity" in {

      cart ! AddArticle(HotDog, 2)

      expectMsg(ArticleAdded(HotDog, 2))
    }

    "sum quantities of the same product" in {

      waitingFor[ArticleAdded] {
        cart ! AddArticle(HotDog, 1)
      }

      waitingFor[ArticleAdded] {
        cart ! AddArticle(HotDog, 2)
      }

      cart ! GetContent

      expectMsg(CartContent(HotDog -> 3))
    }

    "remove an existing product" in {

      waitingFor[ArticleAdded] {
        cart ! AddArticle(Flight)
      }

      cart ! RemoveArticle(Flight)

      expectMsg(ArticleRemoved(Flight, 1))

    }

    "do nothing if asked to remove a product that is not present" in {

      cart ! RemoveArticle(Flight)

      expectNoMsg(1 second)
    }

    "subtract quantities of the same product" in {

      waitingFor[ArticleAdded] {
        cart ! AddArticle(HotDog, 2)
      }

      waitingFor[ArticleRemoved] {
        cart ! RemoveArticle(HotDog, 1)
      }

      cart ! GetContent
      expectMsg(CartContent(HotDog -> 1))
    }

    "remove product from cart is its quantity is 0" in {

      waitingFor[ArticleAdded] {
        cart ! AddArticle(HotDog, 2)
      }

      waitingFor[ArticleRemoved] {
        cart ! RemoveArticle(HotDog, 2)
      }

      val content = waitingFor[CartContent] {
        cart ! GetContent
      }

      content should be('empty)
    }

    "be cleared" in {

      waitingFor[ArticleAdded] {
        cart ! AddArticle(HotDog, 2)
      }

      cart ! Clear
      expectMsg(CartCleared)

      val content = waitingFor[CartContent] {
        cart ! GetContent
      }

      content should be('empty)
    }

    "reject unknown commands" in {

      cart ! "Hello world"

      expectMsg(UnknownCommand("Hello world"))
    }
  }

  "A non-empty cart " which {

    val cartId = nextId("my-cart")

    "is killed at some time" in {
      val persistent = createCartActorWithId(cartId)

      waitingFor[ArticleAdded] {
        persistent ! AddArticle(HotDog, 2)
      }

      watch(persistent)
      waitingFor[Terminated] {
        persistent ! PoisonPill
      }
      unwatch(persistent)
    }

    "is then recovered once it's started again" in {

      val persistent = createCartActorWithId(cartId)

      persistent ! GetContent
      expectMsg(CartContent(HotDog -> 2))
    }
  }
}



