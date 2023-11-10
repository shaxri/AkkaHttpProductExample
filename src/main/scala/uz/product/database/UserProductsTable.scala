package uz.product.database

import uz.product.model.UserProduct

import slick.jdbc.PostgresProfile.api._
import uz.product.model.Product
import uz.product.model.User

class UserProductsTable(tag: Tag) extends Table[UserProduct](tag, "user_products") {
  def userId = column[Long]("user_id")
  def productId = column[Long]("product_id")

  def * = (userId, productId) <> (UserProduct.tupled, UserProduct.unapply)

  def pk = primaryKey("user_product_pk", (userId, productId))
  def user = foreignKey("user_product_user_fk", userId, users)(_.id)
  def product = foreignKey("user_product_product_fk", productId, products)(_.id)

  // Reference to the UsersTable and ProductsTable
  def users = TableQuery[UsersTable]
  def products = TableQuery[ProductsTable]
}
