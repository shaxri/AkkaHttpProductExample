package uz.product.database

import slick.jdbc.PostgresProfile.api._
import uz.product.model.User

// Users table definition
class UsersTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def password = column[String]("password")
  def role = column[String]("role")
  def * = (id, username, password, role) <> (User.tupled, User.unapply)
}
