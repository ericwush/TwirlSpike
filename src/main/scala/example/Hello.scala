package example

import java.io.{OutputStream, InputStream, ByteArrayInputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import net.kaliber.pdf.PdfRenderer

import scala.io.Source

class Hello {

  def exec(is: InputStream, os: OutputStream): Unit = {
    val examples = Paths.get("./sample")
    val `test.pdf` = examples resolve "test.pdf"

    //  private val page: _root_.play.twirl.api.HtmlFormat.Appendable = example.html.hello("Eric", new java.util.Date())
    val page = example.html.invoice("007", new java.util.Date())
    println("A rendered Twirl HTML template:")
    val htmlString: String = page.toString
    println(htmlString)

    val html: ByteArrayInputStream = new ByteArrayInputStream(htmlString.getBytes(StandardCharsets.UTF_8))

    val body = Source.fromInputStream(html).mkString

    val pdf: Array[Byte] = {
      val renderer: PdfRenderer = new PdfRenderer(this.getClass.getClassLoader)
      renderer.toBytes(body)
    }

    val s3Client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider())
    val metadata: ObjectMetadata = new ObjectMetadata()
    metadata.addUserMetadata("email", "eric.wu@myob.com")
    s3Client.putObject("myob-hackday1", "invoice.pdf", new ByteArrayInputStream(pdf), metadata)
  }

}
