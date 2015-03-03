package net.badprogrammer.platform.shoppingcart.view

import akka.actor._
import akka.persistence.Update
import net.badprogrammer.platform.shoppingcart.TestingFixture
import net.badprogrammer.platform.shoppingcart.aggregate.ShoppingCartAggregate
import net.badprogrammer.platform.shoppingcart.command.{AddArticle, CartEvent, Clear, RemoveArticle}
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, Article, Money}
import net.badprogrammer.platform.shoppingcart.query.{GetSummary, Pricing}
import net.badprogrammer.platform.shoppingcart.testsupport.PersistentActorSpec
import org.scalatest.{GivenWhenThen, BeforeAndAfterEach}

import scala.language.implicitConversions

class ShoppingCartPriceViewSpec extends PersistentActorSpec with BeforeAndAfterEach with GivenWhenThen {

  var cart: ActorRef = _

  var view: ActorRef = _

  override protected def beforeEach(): Unit = {
    val id = generatedId
    cart = createCartActorWithId(id)
    view = createViewActorWithId(id)
  }

  def createCartActorWithId(id: String): ActorRef = {
    system.actorOf(ShoppingCartAggregate.props(ShoppingCartId(id), TestingFixture.FakeArticleRepository(system)))
  }

  def createViewActorWithId(id: String): ActorRef = {
    system.actorOf(ShoppingCartPriceView.props(ShoppingCartId(id)))
  }

  override protected def afterEach(): Unit = {
    cart ! PoisonPill
    view ! PoisonPill
  }

  val Flight = TestingFixture.Flight
  val HotDog = TestingFixture.HotDog

  private def givenCartContaining(content: (Article, Int)*) {

    ignoreMsg { case e: CartEvent => true}

    content.foreach { el => cart ! AddArticle(el._1, el._2)}

    waitForUpdatedView()
  }

  private def waitForUpdatedView(): Unit = {
    // Ugly hack! There should be a better way...
    Thread.sleep(30)
    view ! Update(await = true)
    Thread.sleep(30)
  }

  "a price view of an empty shopping cart" must {

    "have total price of 0 " in {

      val summary = waitingFor[Pricing] {
        view ! GetSummary
      }

      summary.total shouldBe Money("0")
    }

    "have no items on it" in {

      val summary = waitingFor[Pricing] {
        view ! GetSummary
      }

      summary.elements should be('empty)
    }

  }

  "a price view of a shopping cart containing one flight" must {

    "have total price of 50.50" in {


      givenCartContaining(Flight -> 1)

      val summary = waitingFor[Pricing] {
        view ! GetSummary
      }

      summary.total shouldBe Money("50.50")

    }

    "have one item on it" in {

      givenCartContaining(Flight -> 1)

      val summary = waitingFor[Pricing] {
        view ! GetSummary
      }

      summary.elements should (have size 1 and contain(Pricing.Element(Flight -> 1, Money("50.50"))))
    }
  }

  "a price view of a shopping cart containing one flight and two hot-dog" must {

    def givenCartContainingTheseTwoArticles: Unit = givenCartContaining(Flight -> 1, HotDog -> 2)

    "have total price of (50.50 + 2 * 10.10) = 70.70" in {

      givenCartContainingTheseTwoArticles

      val summary = waitingFor[Pricing] {
        view ! GetSummary
      }

      summary.total shouldBe Money("70.70")

    }

    "have two items on it" in {

      givenCartContainingTheseTwoArticles

      val summary = waitingFor[Pricing] {
        view ! GetSummary
      }

      summary.elements should (have size 2 and contain allOf(Pricing.Element(Flight -> 1, Money("50.50")), Pricing.Element(HotDog -> 2, Money("10.10"))))
    }

    "update its price to (50.50 + 10.10) = 60.60 when an hot-dog is removed" in {

      givenCartContainingTheseTwoArticles

      cart ! RemoveArticle(HotDog)

      waitForUpdatedView()

      val summary = waitingFor[Pricing] {
        view ! GetSummary
      }

      summary.total shouldBe Money("60.60")
      summary.elements should (have size 2 and contain allOf(Pricing.Element(Flight -> 1, Money("50.50")), Pricing.Element(HotDog -> 1, Money("10.10"))))
    }

    "remove all after been cleared" in {

      givenCartContainingTheseTwoArticles

      cart ! Clear

      waitForUpdatedView()

      val summary = waitingFor[Pricing] {
        view ! GetSummary
      }

      summary.total shouldBe Money.Zero

      summary.elements should be('empty)

    }
  }
}



