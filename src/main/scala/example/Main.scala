package example

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import net.kaliber.pdf.PdfRenderer

import scala.io.Source

object Main extends App {

  val examples = Paths.get("./sample")
  val `test.pdf` = examples resolve "test.pdf"

  private val page: _root_.play.twirl.api.HtmlFormat.Appendable = example.html.hello("Eric", new java.util.Date())
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
}
