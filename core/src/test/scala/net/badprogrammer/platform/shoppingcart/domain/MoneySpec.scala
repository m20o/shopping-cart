package net.badprogrammer.platform.shoppingcart.domain

import net.badprogrammer.platform.shoppingcart.testsupport.CompleteWordSpec


class MoneySpec extends CompleteWordSpec {

  "Money" should {

    "be created from a String" in {

      val amount = Money("10.1")

      amount.cents should be(1010)
    }

    "be summable" in {

      val first = Money("10.10")
      val second = Money("9.90")

      val result = first + second

      result.cents should be(2000)
    }

    "be subtractable" in {

      val first = Money("10.10")
      val second = Money("0.10")

      val result = first - second

      result shouldEqual Money("10")
    }

    "be rounded as 2 decimals half-even" in {

      val amount = Money("10.105")

      amount.cents should be(1010)

    }

    "be comparable" in {

      val ten = Money("10")
      val eleven = Money("11")

      ten should be < eleven
    }

    "be invariant on scale" in {

      val ten = Money("10.11")
      val justTen = Money("10.110000000")

      ten shouldBe justTen
    }
  }
}
