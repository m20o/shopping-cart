package net.badprogrammer.shoppingcart.service

import akka.actor.ActorRef
import net.badprogrammer.platform.testsupport.PersistentActorSpec
import net.badprogrammer.shoppingcart.TestingFixture._
import net.badprogrammer.shoppingcart.command.{AddArticle, ArticleAdded}
import net.badprogrammer.shoppingcart.domain.{ShoppingCartId, User}
import net.badprogrammer.shoppingcart.service.Cart.{Created, DoesNotExists, Exists, _}
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

      val u = user("fancycustomer@gmail.com")

      val id = createCartFor(u)

      waitAtMost(200 millis, Some(id)) {
        ref ! FindByUser(u)
      }
    }

    "return NONE if cart does not exists for an user" in {
      val second: User = user("none@gmail.com")

      ref ! FindByUser(second)

      expectMsgAllOf(None)
    }

    "forward all commands to the specified cart" in {

      val (first, second) = (createCartFor(user("first@gmail.com")), createCartFor(user("second@gmail.com")))

      ref ! Execute(first, AddArticle(Flight))
      ref ! Execute(second, AddArticle(HotDog))

      expectMsgAllConformingOf(classOf[ArticleAdded], classOf[ArticleAdded])
    }
  }

  "A non-empty shopping cart registry actor" which {

    var terminator: ShoppingCartId = null

    "is stopped at some time" in {

      terminator = createCartFor(user("t1000@cyberdyne.com"))

      terminate(ref)
    }

    "is recovered in the same state once it's started again" in {
      ref ! Check(terminator)
      expectMsg(Exists)
    }
  }
}
