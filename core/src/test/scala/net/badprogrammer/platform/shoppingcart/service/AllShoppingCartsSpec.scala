package net.badprogrammer.platform.shoppingcart.service

import akka.actor.{ActorRef, PoisonPill, Terminated}
import net.badprogrammer.platform.shoppingcart.TestingFixture._
import net.badprogrammer.platform.shoppingcart.command._
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, User}
import net.badprogrammer.platform.shoppingcart.service.Cart._
import net.badprogrammer.platform.shoppingcart.testsupport.PersistentActorSpec
import org.scalatest.BeforeAndAfter

class AllShoppingCartsSpec extends PersistentActorSpec with BeforeAndAfter {

  var ref: ActorRef = _

  def user(id: String) = User(id, "GREAT_SITE")

  after {
    watch(ref)
    ref ! PoisonPill
    expectMsgType[Terminated]
    unwatch(ref)
  }

  before {
    ref = system.actorOf(AllShoppingCarts.props(FakeArticleRepository(system)))
  }

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

      val id = waitingFor[Created] {
        ref ! Create(user("onmyway@gmail.com"))
      }.id


      ref ! Check(id)
      ref ! Check(ShoppingCartId("whatever"))

      expectMsgAllOf(Exists, DoesNotExists)

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
        ref ! Create(user("terminator-t1000@cyberdyne.com"))
      }.id

      watch(ref)
      waitingFor[Terminated] {
        ref ! PoisonPill
      }
      unwatch(ref)

    }

    "is recovered in the same state once it's started again" in {

     ref ! Check(terminator)
      expectMsg(Exists)
    }
  }

}
