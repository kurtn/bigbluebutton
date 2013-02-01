package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def create = Action {
    Ok(views.html.index("Create handler not yet implemented."))
  }
  
  def join = Action {
    Ok(views.html.index("Join handler not yet implemented."))
  }
  
  def enter = Action {
    Ok(views.html.index("Enter handler not yet implemented."))
  }
}