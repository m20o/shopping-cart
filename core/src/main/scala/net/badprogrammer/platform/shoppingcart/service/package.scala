package net.badprogrammer.platform.shoppingcart

import net.badprogrammer.platform.shoppingcart.command.Create
import net.badprogrammer.platform.shoppingcart.domain.ShoppingCartId

package object service {

  type ShoppingCartIdFactory = (Create) => ShoppingCartId
  
  object DefaultShoppingCartIdFactory extends ShoppingCartIdFactory {
    
    override def apply(c: Create): ShoppingCartId = ShoppingCartId(c)

  }
  
}
