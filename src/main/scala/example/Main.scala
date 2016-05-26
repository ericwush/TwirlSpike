package example

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import net.kaliber.pdf.PdfRenderer

import scala.io.Source

object Main extends App {

  val examples = Paths.get("./sample")
  val `test.pdf` = examples resolve "test.pdf"

//  private val page: _root_.play.twirl.api.HtmlFormat.Appendable = example.html.hello("Eric", new java.util.Date())
  private val page = example.html.invoice("007", new java.util.Date())
  println("A rendered Twirl HTML template:")
  private val htmlString: String = page.toString
  println(htmlString)

  private val html: ByteArrayInputStream = new ByteArrayInputStream(htmlString.getBytes(StandardCharsets.UTF_8))

  val body = Source.fromInputStream(html).mkString

  val pdf: Array[Byte] = {
    val renderer: PdfRenderer = new PdfRenderer(this.getClass.getClassLoader)
    renderer.toBytes(body)
  }

  Files write (`test.pdf`, pdf)

//  val s3Client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider())
//  private val metadata: ObjectMetadata = new ObjectMetadata()
//  metadata.addUserMetadata("email", "eric.wu@myob.com")
//  s3Client.putObject("myob-hackday", "invoice.pdf", new ByteArrayInputStream(pdf), metadata)
}
