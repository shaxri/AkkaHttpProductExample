package uz.product.service

import slick.jdbc.PostgresProfile.api._
import uz.product.database.UsersTable
import uz.product.model.{LoginRequest, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserService(db: Database) {

  private val Users = TableQuery[UsersTable]

  def login(loginRequest: LoginRequest): Future[Option[User]] = {
    db.run(Users.filter(user => user.username === loginRequest.username && user.password === loginRequest.password).result.headOption)
  }

  def hasUserRole(userId: Long, role: String): Future[Boolean] = {
    db.run(Users.filter(user => user.id === userId && user.role === role).result.headOption).map(_.isDefined)
  }

  // Replace this with your actual authentication logic
  def authenticateUserByToken(token: String): Future[Option[User]] = {
    // Example: Retrieve user based on the token from the database
    val query = Users.filter(_.password === token) // Assuming password is used as a token for simplicity
    db.run(query.result.headOption)
  }

}




