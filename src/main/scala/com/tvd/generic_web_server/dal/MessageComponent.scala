package com.tvd.generic_web_server.dal

import scala.concurrent.ExecutionContext.Implicits.global
import com.tvd.generic_web_server.model.{Message, User}
import com.tvd.generic_web_server.dal.UserComponent

import slick.lifted.{ForeignKeyQuery, ProvenShape}

trait MessageComponent { this: ProfileComponent with UserComponent =>
  import profile.api._

  class MessageTable(tag: Tag) extends Table[Message](tag, "MESSAGE") {
    def messageId: Rep[Long] = column[Long]("MESSAGE_ID", O.PrimaryKey, O.AutoInc)
    def senderId: Rep[Long] = column[Long]("SENDER_ID")
    def content: Rep[String] = column[String]("CONTENT")
    def sender: ForeignKeyQuery[UserTable, User] = foreignKey("sender_fk", senderId, TableQuery[UserTable])(_.userId)

    def * : ProvenShape[Message] = (messageId, senderId, content).mapTo[Message]
  }

  val messages = TableQuery[MessageTable]

  private val messagesAutoInc = messages returning messages.map(_.messageId)

//  def insert(user: User): DBIO[User] = for {
//    //#insert
//    pic <-
//      if(user.userId.isEmpty) insert(user.)
//      else DBIO.successful(user.picture)
//    //#insert
//    id <- usersAutoInc += (user.name, pic.id.get)
//  } yield user.copy(picture = pic, id = id)

  //  def insert(message: Message): DBIO[Message] =
//    (picturesAutoInc += message).map(id => message.copy(messageId = id))
}