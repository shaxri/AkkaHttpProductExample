import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.Credentials
import akka.util.Timeout
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import uz.product.database.UsersTable
import uz.product.model.{LoginRequest, Product, User}
import uz.product.service.{ProductService, UserService}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

trait JsonFormats extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val loginRequestFormat: RootJsonFormat[LoginRequest] = jsonFormat2(LoginRequest)
  implicit val productFormat: RootJsonFormat[Product] = jsonFormat4(Product)
}

object ProductApplication extends App with JsonFormats {

  // Your Slick database configuration
  val db: Database = Database.forConfig("postgresql")

  implicit val system: ActorSystem = ActorSystem("AkkaHttpProductExample")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)

  val userService = new UserService(db)
  val productService = new ProductService(db)

  // Seed data
  val seedData = Seq(
    User(1, "admin", "admin_password", "ADMIN"),
    User(2, "user", "user_password", "USER")
  )

  val users = TableQuery[UsersTable]


  val routes =
    path("login") {

      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, loadResource("html/login.html")))
      } ~
        post {
          formFields("username", "password") { (username, password) =>
            onComplete(userService.login(LoginRequest(username, password))) {
              case Success(Some(user)) => complete(StatusCodes.OK -> s"Welcome, ${user.username}!")
              case _ => complete(StatusCodes.Unauthorized -> "Invalid credentials")
            }

          }
        }

    } ~
      path("products") {
        authenticateOAuth2Async("realm", authenticateUser) { user =>
          concat(
            get {
              // Your logic for handling the request for authenticated users
              onComplete(productService.getProductsForUser(user.id)){
                case Success(products) =>
                  complete(StatusCodes.OK -> s"Products for userId: $products")
                case Failure(ex) =>
                  complete(StatusCodes.InternalServerError -> s"Failed to fetch products: ${ex.getMessage}")
              }
            },
            post {
              entity(as[Product]) { product =>
                // Your logic for handling the request for authenticated users
                onComplete(productService.addProduct(product)) {
                  case Success(insertedId) =>
                    complete(StatusCodes.Created -> s"Product added with ID: $insertedId")
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError -> s"Failed to add product: ${ex.getMessage}")
                }
              }
            },
            put {
              // Your logic for handling the request for authenticated users
              entity(as[Product]) { product =>
                onComplete(productService.updateProduct(product)) {
                  case Success(updatedId) =>
                    complete(StatusCodes.Created -> s"Product update with ID: $updatedId")
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError -> s"Failed to update product: ${ex.getMessage}")
                }
              }
            },
            delete {
              // Your logic for handling the request for authenticated users
              parameters('productId.as[Int]) { productId =>
                onComplete(productService.deleteProduct(productId)) {
                  case Success(deletedId) =>
                    complete(StatusCodes.Created -> s"Product update with ID: $deletedId")
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError -> s"Failed to update product: ${ex.getMessage}")
                }
              }
            }
          )
        }
      }

  def authenticateUser(credentials: Credentials): Future[Option[User]] = credentials match {
    case Credentials.Provided(token) =>
      userService.authenticateUserByToken(token)
        .map {
          case Some(user) => Some(user)
          case None => None
        }
    case _ => Future.successful(None)
  }

  private def loadResource(resourceName: String): String = {
    val resourceStream = getClass.getClassLoader.getResourceAsStream(resourceName)
    scala.io.Source.fromInputStream(resourceStream).mkString
  }

  Http().newServerAt("localhost", 8080).bind(routes)

  sys.addShutdownHook {
    db.close()
  }
}
