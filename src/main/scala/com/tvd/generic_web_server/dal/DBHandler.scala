package com.tvd.generic_web_server.dal

import com.tvd.generic_web_server.model.User
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration._

object DBHandler {

  val db = Database.forConfig("h2mem1")

//  try {
//    Await.result(db.run(DBIO.seq(
//      // create the schema
//      users.schema.create,
//
//      // insert two User instances
//      users += User(1, "", "", "","Fred Smith", "", "", "", ""),
//      users += User(2, "", "", "","Fred Smith", "", "", "", ""),
//
//      // print the users (select * from USERS)
//      users.result.map(println)
//    )), Duration.Inf)
//  } finally db.close

//  val db = DatabaseConfig.forConfig("h2")
//
//  val slickSession: SlickSession = SlickSession.forConfig(db)
//
//  def createTables(projectionTables: List[String]): Unit = {
//    Await.ready(slickSession.db.run(dropTables(projectionTables)), 30.seconds)
//    Await.ready(slickSession.db.run(createJournal), 30.seconds)
//    projectionTables.map{tableName =>
//      Await.ready(slickSession.db.run(createProjection(tableName)), 30.seconds)
//    }
//    Await.ready(slickSession.db.run(createSnapshot), 30.seconds)
//  }
//
//  def dropTables(projectionTableNames: List[String]): DBIO[Int] = sqlu"""
//    DROP TABLE IF EXISTS journal, snapshot, #${projectionTableNames.mkString(", ")};
//    """
//
//  def createProjection(tableName: String): DBIO[Int] = sqlu"""
//    CREATE TABLE IF NOT EXISTS #$tableName (
//        event VARCHAR(255) NOT NULL
//    );
//    """
//
//  val createSnapshot: DBIO[Int] = sqlu"""
//    CREATE TABLE IF NOT EXISTS snapshot (
//      persistence_id VARCHAR(255) NOT NULL,
//     sequence_number BIGINT NOT NULL,
//      created BIGINT NOT NULL,
//      snapshot BLOB NOT NULL,
//      PRIMARY KEY (persistence_id, sequence_number)
//    );
//    """
//
//  val createJournal: DBIO[Int] = sqlu"""
//    CREATE TABLE IF NOT EXISTS journal (
//      ordering SERIAL,
//      persistence_id VARCHAR(255) NOT NULL,
//      sequence_number BIGINT NOT NULL,
//      deleted BOOLEAN DEFAULT FALSE,
//      tags VARCHAR(255) DEFAULT NULL,
//      message BLOB NOT NULL,
//      PRIMARY KEY(persistence_id, sequence_number)
//    );
//    """
}
