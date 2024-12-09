package ch.epfl.scala.bsp.testkit.gen

import java.net.URI
import java.nio.file.{Path, Paths}

import org.scalacheck.Shrink
import org.scalacheck.Shrink.shrink

import scala.collection.JavaConverters._

trait UtilShrinkers {

  def shrinkRight[T]: Shrink[Iterable[T]] = Shrink { stream =>
    val most = stream.dropRight(1)
    if (most.isEmpty) Stream(most)
    else most #:: shrinkRight.shrink(most)
  }

  def shrinkUriPath: Shrink[String] = Shrink { path =>
    val parts = path.split("/")
    for {
      p <- shrinkRight.shrink(parts)
    } yield p.mkString("/")
  }

  def shrinkUriPartToLetters: Shrink[String] = Shrink { host =>
    Shrink
      .shrinkContainer[List, Char]
      .suchThat(shrinkedHost => shrinkedHost.nonEmpty && shrinkedHost.forall(_.isLetter))
      .shrink(host.toList)
      .map(_.mkString)
  }

  def shrinkPortInt: Shrink[Int] = Shrink { x: Int =>
    shrink(x)
  }.suchThat(_ > 0)

  implicit def shrinkUri(implicit s1: Shrink[String], s2: Shrink[Int]): Shrink[URI] = Shrink {
    uri =>
      val shrinks = for {
        scheme <- shrinkUriPartToLetters.shrink(uri.getScheme)
        host <- shrinkUriPartToLetters.shrink(uri.getHost)
        port <- shrinkPortInt.shrink(uri.getPort)
        path <- shrinkUriPath.shrink(uri.getPath)
      } yield {
        val a = new URI(scheme, null, host, port, path, null, null)
        val b = new URI(scheme, null, host, port, null, null, null)
        Stream(a, b)
      }

      shrinks.flatten
  }

  implicit def shrinkPath: Shrink[Path] = Shrink { path =>
    val partIt = path.iterator()
    if (partIt.hasNext && !path.startsWith(""))
      shrinkRight.shrink(path.iterator.asScala.toIterable).map { parts =>
        Paths.get(parts.mkString("/"))
      }
    else Stream.empty
  }

  def shrinkFileUri: Shrink[URI] = Shrink { uri: URI =>
    val path = Paths.get(uri)
    shrinkPath.shrink(path).map(_.toUri)
  }

  def shrinkFileUriString: Shrink[String] = Shrink { uriStr: String =>
    val uri = URI.create(uriStr)
    shrinkFileUri.shrink(uri).map(_.toString)
  }
}

object UtilShrinkers extends UtilShrinkers
