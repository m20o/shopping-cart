package setup

import com.typesafe.config.ConfigFactory
import net.badprogrammer.platform.shoppingcart.LocalShoppingCartSystemFactory
import net.badprogrammer.shoppingcart.api.{LocalShoppingCartSystemFactory, ShoppingCartSystem}
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {

  var cartSystem: ShoppingCartSystem = _

  override def onStart(app: Application) = {
    cartSystem = LocalShoppingCartSystemFactory(ConfigFactory.load("conf/shoppingcart.conf"))
  }


  override def onStop(app: Application): Unit = cartSystem.terminate



  override def getControllerInstance[A](controllerClass: Class[A]): A = super.getControllerInstance(controllerClass)

}
