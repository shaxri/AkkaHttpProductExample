package uz.product.database

import slick.jdbc.PostgresProfile.api._
import uz.product.model.Product

import scala.concurrent.Future
// Products table definition
class ProductsTable(tag: Tag) extends Table[Product](tag, "products") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def itemName = column[String]("item_name")
  def quantity = column[Int]("quantity")
  def price = column[Double]("price")
  def * = (id, itemName, quantity, price) <> (Product.tupled, Product.unapply)
}
