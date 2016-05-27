package example

import java.io.{ByteArrayInputStream, InputStream, OutputStream}
import java.nio.charset.StandardCharsets

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import model.Models.Records
import net.kaliber.pdf.PdfRenderer
import org.apache.commons.io.IOUtils
import play.api.libs.json._

import scala.io.Source
import scala.util.{Failure, Success, Try}

class Hello {

  def exec(is: InputStream, os: OutputStream): Unit = {

    Try(IOUtils.toString(is, "UTF-8")) match {
      case Success(input: String) =>
        val jsValue: JsValue = Json.parse(input)
        val records: Records = Json.fromJson[Records](jsValue).get
        val pdf: Array[Byte] = generate(records)
        store(pdf)
      case Failure(e) =>
        println(s"${e.getClass.getName}: ${e.getMessage}")
    }

    def generate(records: Records): Array[Byte] = {
      val page = example.html.invoice("007", records)
      println("A rendered Twirl HTML template:")
      val htmlString: String = page.toString
      println(htmlString)

      val html: ByteArrayInputStream = new ByteArrayInputStream(htmlString.getBytes(StandardCharsets.UTF_8))

      val body: String = Source.fromInputStream(html).mkString

      val pdf: Array[Byte] = {
        val renderer: PdfRenderer = new PdfRenderer(this.getClass.getClassLoader)
        renderer.toBytes(body)
      }
      pdf
    }

    def store(pdf: Array[Byte]) : Unit = {
      val s3Client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider())
      val metadata: ObjectMetadata = new ObjectMetadata()
      metadata.addUserMetadata("email", "eric.wu@myob.com")
      s3Client.putObject("myob-hackday1", "invoice.pdf", new ByteArrayInputStream(pdf), metadata)
    }

  }

}
