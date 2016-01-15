import org.specs2._

import models._

class IpSpec extends Specification { def is=s2"""

  Ip should

    parse an IPv4                                   $parse4
    parse an IPv6                                   $parse6
    fail parsing a wrong IP                         $parseN

    remove the last two bytes of an IPv4 string     $removeLastBytes4
    remove the last two bytes of an IPv6 string     $removeLastBytes6
    return None when the given string is not an IP  $removeLastBytesN
"""

  def parse4 = {
    Ip.parse("192.168.1.24") must beSome
  }

  def parse6 = {
    Ip.parse("2001:0db8:85a3::8a2e:0370:7334") must beSome
  }

  def parseN = {
    (Ip.parse("192.BE.A0.168") === None) and
    (Ip.parse("2001::0db8::1") === None)
  }

  def removeLastBytes4 = {
    Ip.removeLastBytes("192.168.1.24") === Some("192.168")
  }

  def removeLastBytes6 = {
    Ip.removeLastBytes("2001:0db8:85a3::8a2e:0370:7334") === Some("2001:db8:85a3")
  }

  def removeLastBytesN = {
    Ip.removeLastBytes("Bla Bla") === None
  }

}
