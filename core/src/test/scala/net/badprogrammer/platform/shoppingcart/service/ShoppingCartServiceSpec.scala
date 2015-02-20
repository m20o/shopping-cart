package net.badprogrammer.platform.shoppingcart.service

import net.badprogrammer.platform.shoppingcart.TestingFixture._
import net.badprogrammer.platform.shoppingcart.command.Cart.{Check, DoesNotExists, Exists}
import net.badprogrammer.platform.shoppingcart.command._
import net.badprogrammer.platform.shoppingcart.domain.ShoppingCartId
import net.badprogrammer.platform.shoppingcart.testsupport.ActorSpec
import org.scalatest.BeforeAndAfter

class ShoppingCartServiceSpec extends ActorSpec with BeforeAndAfter {

  val ref = system.actorOf(ShoppingCartService.props(FakeArticleRepository(system), DefaultShoppingCartIdFactory))

  "Shopping cart service" must {

    "create a new shopping cart" in {

      ref ! Create("MY-SITE", "user@gmail.com")

      val created = expectMsgType[Created]

      created.id.value should not be 'empty
    }

    "refuse to create cart twice for the same user and site" in {

      ref ! Create("MY-SITE", "user@gmail.com")

      expectMsg(Exists)

    }

    "create new cart for the same user on different site" in {

      ref ! Create("ANOTHER-SITE", "user@gmail.com")

      expectMsgType[Created]

    }

    "check whether a cart exists or not" in {

      ref ! Create("MY-SITE", "onmyway@gmail.com")

      val id = expectMsgType[Created].id

      ref ! Check(id)
      ref ! Check(ShoppingCartId("whatever"))

      expectMsgAllOf(Exists, DoesNotExists)

    }

    "forward all commands to the specified cart" in {

      ref ! Create("V-SITE", "first@gmail.com")
      ref ! Create("V-SITE", "second@gmail.com")

      val (first, second) = {
        val res = expectMsgAllClassOf(classOf[Created], classOf[Created]).map(_.id)
        (res(0), res(1))
      }

      ref ! Execute(first, AddArticle(Flight))
      ref ! Execute(second, AddArticle(HotDog))

      expectMsgAllConformingOf(classOf[ArticleAdded], classOf[ArticleAdded])
    }
  }
}
