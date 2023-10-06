package com.tvd.generic_web_server.model

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}

@JsonCreator
final case class User(@JsonProperty("user_id") userId: Long,
                      @JsonProperty("username") username: String,
                      @JsonProperty("password") password: String,
                      @JsonProperty("firstname") firstName: String,
                      @JsonProperty("lastname") lastName: String,
                      @JsonProperty("street") street: String,
                      @JsonProperty("city") city: String,
                      @JsonProperty("state") state: String,
                      @JsonProperty("zip") zip: String)

@JsonCreator
final case class Users(@JsonProperty("users") users: Seq[User])