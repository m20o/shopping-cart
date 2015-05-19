package net.badprogrammer.shoppingcart.service

import akka.actor.ActorRef
import net.badprogrammer.shoppingcart.TestingFixture
import TestingFixture._
import Cart._
import net.badprogrammer.platform.testsupport.PersistentActorSpec
import net.badprogrammer.shoppingcart.command.{AddArticle, ArticleAdded}
import net.badprogrammer.shoppingcart.domain.{User, ShoppingCartId}
import net.badprogrammer.shoppingcart.service.Cart.{DoesNotExists, Exists, Created}
import org.scalatest.BeforeAndAfter

import scala.collection.mutable.Map
import scala.concurrent.duration._
import scala.language.postfixOps

class ShoppingCartsHandlerSpec extends PersistentActorSpec with BeforeAndAfter {

  var ref: ActorRef = _

  def user(id: String, site: String = "GREAT_SITE") = User(id, site)

  after {
    terminate(ref)
  }

  before {
    val repository: ActorRef = FakeArticleRepository(system)
    val storage = new CartsByUserStorage {

      val map: Map[User, ShoppingCartId] = Map.empty[User, ShoppingCartId]

      def findByUser(user: User): Option[ShoppingCartId] = map.get(user)

      def insert(user: User, id: ShoppingCartId): Unit = map += (user -> id)
    }

    ref = system.actorOf(ShoppingCartsHandler.props(repository, storage))
  }

  def createCartFor(user: User): ShoppingCartId = waitingFor[Created](ref ! Create(user)).id

  "A shopping cart registry actor" must {

    "create a new shopping cart" in {

      ref ! Create(User(context = "MY-SITE", id = "user@gmail.com"))

      val created = expectMsgType[Created]

      created.id.value should not be 'empty
    }

    "refuse to create cart twice for the same user and site" in {

      waitingFor[Created] {
        ref ! Create(User(context = "MY-GORGEOUS-SITE", id = "user@gmail.com"))
      }

      ref ! Create(User(context = "MY-GORGEOUS-SITE", id = "user@gmail.com"))
      expectMsg(Exists)
    }

    "create new cart for the same user on different site" in {

      ref ! Create(User(context = "ANOTHER-SITE", id = "user@gmail.com"))
      expectMsgType[Created]

    }

    "check whether a cart exists or not" in {

      val id = createCartFor(user("onmyway@gmail.com"))

      ref ! Check(id)
      ref ! Check(ShoppingCartId("whatever"))

      expectMsgAllOf(Exists, DoesNotExists)
    }

    "find existing carts by user" in {

      val first: User = user("fancycustomer@gmail.com")
      val second: User = user("none@gmail.com")

      val id = createCartFor(first)

      waitFor(200 millis)

      ref ! FindByUser(first)
      ref ! FindByUser(second)
      expectMsgAllOf(Some(id), None)
    }

    "forward all commands to the specified cart" in {

      ref ! Create(user("first@gmail.com"))
      ref ! Create(user("second@gmail.com"))

      val (first, second) = {
        val res = expectMsgAllClassOf(classOf[Created], classOf[Created]).map(_.id)
        (res.head, res(1))
      }

      ref ! Execute(first, AddArticle(Flight))
      ref ! Execute(second, AddArticle(HotDog))

      expectMsgAllConformingOf(classOf[ArticleAdded], classOf[ArticleAdded])
    }
  }

  "A non-empty shopping cart registry actor" which {

    var terminator: ShoppingCartId = null

    "is stopped at some time" in {

      terminator = waitingFor[Created] {
        ref ! Create(user("t1000@cyberdyne.com"))
      }.id

      terminate(ref)
    }

    "is recovered in the same state once it's started again" in {
      ref ! Check(terminator)
      expectMsg(Exists)
    }
  }
}
