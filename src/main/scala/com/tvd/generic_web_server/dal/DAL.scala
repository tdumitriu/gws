package com.tvd.generic_web_server.dal

import slick.jdbc.JdbcProfile

/** The Data Access Layer contains all components and a profile */
class DAL(val profile: JdbcProfile) extends UserComponent with MessageComponent with ProfileComponent {
  import profile.api._

  def create =
    (
      users.schema ++
      messages.schema
    ).create
}