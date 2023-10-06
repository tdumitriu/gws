package com.tvd.generic_web_server.security

import java.io.{FileInputStream, IOException}
import java.security.{KeyStore, SecureRandom}

import akka.http.scaladsl.{ConnectionContext, HttpsConnectionContext}
import com.tvd.generic_web_server.WebServer.logger
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

trait HttpsConnection {

  val password: Array[Char] = scala.util.Properties.envOrElse("TVD_SSL_PASSWORD", "" ).toCharArray
  val keystoreFilename: String = scala.util.Properties.envOrElse("TVD_KEYSTORE", "." )

  val ks: KeyStore = KeyStore.getInstance("PKCS12")

  var keystoreInputStream: Option[FileInputStream] = None: Option[FileInputStream]
  try {
    keystoreInputStream = Some(new FileInputStream(keystoreFilename))
  } catch {
    case e: IOException =>
      logger.error(s"Failed to read the [$keystoreFilename] file.")
      e.printStackTrace()
  }

  require(keystoreInputStream.isDefined, "Keystore required!")
  ks.load(keystoreInputStream.get, password)
  logger.debug(s"The keystore provider is [${ks.getProvider}]")

  keystoreInputStream.get.close()

  val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
  keyManagerFactory.init(ks, password)

  val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
  tmf.init(ks)

  val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
  sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)

  val https: HttpsConnectionContext = ConnectionContext.httpsServer(sslContext)
}
