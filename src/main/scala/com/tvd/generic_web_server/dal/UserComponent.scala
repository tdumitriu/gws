package com.tvd.generic_web_server.dal

import com.tvd.generic_web_server.dal.DBHandler.db
import com.tvd.generic_web_server.model.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait UserComponent { this: ProfileComponent =>
  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "USERS") {
    def userId: Rep[Long]      = column[Long]("USER_ID", O.PrimaryKey, O.AutoInc)
    def username: Rep[String]  = column[String]("USERNAME")
    def password: Rep[String]  = column[String]("PASSWORD")
    def firstname: Rep[String] = column[String]("FIRST_NAME")
    def lastname: Rep[String]  = column[String]("LAST_NAME")
    def street: Rep[String]    = column[String]("STREET")
    def city: Rep[String]      = column[String]("CITY")
    def state: Rep[String]     = column[String]("STATE")
    def zip: Rep[String]       = column[String]("ZIP")

    def * = (userId, username, password, firstname, lastname, street, city, state, zip).mapTo[User]
  }

  val users = TableQuery[UserTable]
  private val usersAutoInc = users returning users.map(_.userId)

  object AccountsDAO extends TableQuery(new UserTable(_)) {
    def get(username: String): Query[UserTable, User, Seq] =
      users.filter(_.username === username).map(_.value)

    def insert(user: User): DBIO[User] =
      (usersAutoInc += user).map(id => user.copy(userId = id))

    def findById(id: Long): Future[Option[User]] = {
      db.run(this.filter(_.userId === id).result).map(_.headOption)
    }
    def create(account: User): Future[User] = {
      db.run(this returning this.map(_.userId) into ((acc, id) => acc.copy(userId = id)) += account)
    }

    def deleteById(id: Long): Future[Int] = {
      db.run(this.filter(_.userId === id).delete)
    }
  }
}