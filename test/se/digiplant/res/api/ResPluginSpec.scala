package se.digiplant.res.api

import se.digiplant.res._
import org.specs2.mutable.Specification
import java.io.{FileInputStream, File}
import org.apache.commons.io.IOUtils
import play.api._
import libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart

object ResPluginSpec extends Specification {

  implicit val ctx = new ResContext

  "Res Plugin" should {

    "start" in {
      ctx.res must beAnInstanceOf[ResPlugin]
    }

    "put a resource" in {
      val fileuid = ctx.res put(ctx.getTestFile)
      fileuid must not beEmpty
      new File("tmp/default/5564/ac5e/5564ac5e3968e77b4022f55a23d36630bdeb0274.jpg").exists() must beTrue
    }

    "put a resource into a supplied source" in {
      val fileuid = ctx.res put(ctx.getTestFile, "images")
      fileuid must not beEmpty
      new File("tmp/images/5564/ac5e/5564ac5e3968e77b4022f55a23d36630bdeb0274.jpg").exists() must beTrue
    }

    "put a resource with metadata" in {
      val fileuid = ctx.res put(ctx.getTestFile, meta = Seq("100", "100", "auto"))
      fileuid must not beEmpty
      new File("tmp/default/5564/ac5e/5564ac5e3968e77b4022f55a23d36630bdeb0274_100_100_auto.jpg").exists() must beTrue
    }

    "put a scala filepart" in {
      val f = FilePart("testPart", "filename", Some("image/jpeg"), TemporaryFile(ctx.getTestFile()))
      val fileuid = ctx.res.put(f, "default", Seq.empty)
      fileuid must not beEmpty
    }

    "put a scala filepart with no extension" in {
      val f = FilePart("testPart", "filename", Some("image/jpeg"), TemporaryFile(ctx.getAnonymousTestFile()))
      val fileuid = ctx.res.put(f, "default", Seq.empty)
      fileuid must not beEmpty
    }

    "get a resource" in {
      val fileuid = ctx.res put(ctx.getTestFile)
      val file = ctx.res get(fileuid)
      IOUtils.contentEquals(new FileInputStream(ctx.logo), new FileInputStream(file.get)) must beTrue
    }

    "get a resource from a supplied source" in {
      val fileuid = ctx.res put(ctx.getTestFile, "images")
      val file = ctx.res get(fileuid, "images")

      IOUtils.contentEquals(new FileInputStream(ctx.logo), new FileInputStream(file.get)) must beTrue
    }

    "get a resource with the supplied metadata" in {
      val fileuid = ctx.res put(ctx.getTestFile, meta = Seq("100", "100", "auto"))
      val file = ctx.res get(fileuid)

      IOUtils.contentEquals(new FileInputStream(ctx.logo), new FileInputStream(file.get)) must beTrue
    }

    "delete a resource" in {
      val fileuid = ctx.res put(ctx.getTestFile)
      var file = new File("tmp/default/5564/ac5e/5564ac5e3968e77b4022f55a23d36630bdeb0274.jpg")
      file.exists() must beTrue

      ctx.res delete(fileuid) must beTrue
      file.exists() must beFalse
    }

    "delete a resource with supplied metadata" in {
      val fileuid = ctx.res put(ctx.getTestFile, meta = Seq("100", "100", "auto"))
      var file = new File("tmp/default/5564/ac5e/5564ac5e3968e77b4022f55a23d36630bdeb0274_100_100_auto.jpg")
      file.exists() must beTrue

      ctx.res delete(fileuid) must beTrue
      file.exists() must beFalse
    }

  }
}
