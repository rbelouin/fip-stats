package models

import scala.util.Try

import java.net.InetAddress

/**
 * In order to be compliant with the French laws,
 * keep only 2 bytes of the given IPv4 address
 * and 6 bytes for an IPv6 address.
 */

object Ip {
  def parse(ip: String): Option[InetAddress] = {
    Try(InetAddress.getByName(ip)).toOption
  }

  def removeLastBytes(ip: String): Option[String] = {
    parse(ip).map(_.getHostAddress).map(address => {
      // IPv6 addresses do not have dots
      if(address.contains('.')) {
        address.split('.').take(2).mkString(".")
      }
      else {
        address.split(':').take(3).mkString(":")
      }
    })
  }
}
