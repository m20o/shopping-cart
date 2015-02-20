package net.badprogrammer.shoppingcart.platform

import net.badprogrammer.platform.shoppingcart.domain._

object TestingFixture {

  val Flight = Article("flight")
  val HotDog = Article("hot-dog")
  val Cocaine = Article("cocaine")

  val pricing = {
    val builder = Map.newBuilder[Article, Money]
    builder += (Flight -> Money("50.50"))
    builder += (HotDog -> Money("10.10"))
    builder.result()
  }

}
