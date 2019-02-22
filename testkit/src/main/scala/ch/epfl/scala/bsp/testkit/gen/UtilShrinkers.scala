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

  implicit def shrinkUri(implicit s1: Shrink[String],
                         s2: Shrink[Int]): Shrink[URI] = Shrink { uri =>
    val shrinks = for {
      scheme <- shrink(uri.getScheme)
      host <- shrink(uri.getHost)
      port <- shrink(uri.getPort)
      path <- shrinkUriPath.shrink(uri.getPath)
    } yield {
      val a = new URI(scheme, null, host, port, path, null, null)
      val b = new URI(scheme, null, null, port, path, null, null)
      val c = new URI(scheme, null, host, port, null, null, null)
      Stream(a,b,c)
    }

    shrinks.flatten
  }

  implicit def shrinkPath: Shrink[Path] = Shrink { path =>
    shrinkRight.shrink(path.iterator.asScala.toIterable).map { parts =>
      Paths.get(parts.mkString("/"))
    }
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
