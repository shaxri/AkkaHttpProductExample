package uz.product.service

import slick.jdbc.PostgresProfile.api._
import uz.product.database.{ProductsTable, UserProductsTable}
import uz.product.model.Product

import scala.concurrent.Future

class ProductService(db: Database) {

  private val Products = TableQuery[ProductsTable]
  private val userProducts = TableQuery[UserProductsTable]


  def getProductsForUser(userId: Long): Future[Seq[Product]] = {
    val query = for {
      userProduct <- userProducts if userProduct.userId === userId
      product <- Products if userProduct.productId === product.id
    } yield product

    db.run(query.result)
  }

  def addProduct(product: Product): Future[Long] = {
    db.run((Products returning Products.map(_.id)) += product)
  }

  def updateProduct(product: Product): Future[Int] = {
    db.run(Products.filter(_.id === product.id).update(product))
  }

  def deleteProduct(productId: Long): Future[Int] = {
    db.run(Products.filter(_.id === productId).delete)
  }

}
