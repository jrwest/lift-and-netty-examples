package bootstrap.liftweb

/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 2/22/12
 * Time: 9:17 PM
 */

import net.liftweb._
import http._
import com.something.lift.MyRest

class Boot {

  def boot {
    println("**** Dude... I'm in Boot!!")
    LiftRules.statelessDispatchTable.append(MyRest)
  }

}