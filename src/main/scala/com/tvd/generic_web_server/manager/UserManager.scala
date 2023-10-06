package com.tvd.generic_web_server.manager

import com.tvd.generic_web_server.Logger
import com.tvd.generic_web_server.model.{User, Users}

object UserManager extends Logger {

  final case class ActionPerformed(description: String)

  var users: Set[User] = Set()

  // CREATE
  def createUser(user: User): ActionPerformed = {
    logger.debug(s"Create user with username [${user.username}]")
    users += user
    ActionPerformed("ok")
  }

  // READ
  def getAllUsers: Users = {
    logger.debug(s"Get all users: ${users.mkString(",")}")
    Users(users.toSeq)
  }

  def getUserById(id: String): Option[User] = {
    logger.debug(s"Get user by id [$id]")
    users.find(_.userId == id.toLong)
  }

  def getUserByUsername(username: String): Option[User] = {
    logger.debug(s"Get user by username [$username]")
    users.find(_.username == username)
  }

  // UPDATE
  def updateUser(user: User): ActionPerformed = {
    logger.debug(s"Update user with id [${user.userId}]")
    users += user
    ActionPerformed("ok")
  }

  // DELETE
  def deleteUserById(id: String): ActionPerformed = {
    logger.debug(s"Delete user by id [$id]")
    users.find(_.userId == id.toLong) foreach { user => users -= user }
    ActionPerformed(s"User with id [$id] deleted")
  }
}
