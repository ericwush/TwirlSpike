package example

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import model.Models.{Record, Records}
import net.kaliber.pdf.PdfRenderer
import play.api.libs.json.Json

import scala.io.Source

object Main extends App {

  val examples = Paths.get("./sample")
  val `test.pdf` = examples resolve "test.pdf"

  val record1: Record = new Record("Service 1", "This is a secrete service", 1, 1000, 1000)
  val record2: Record = new Record("Service 2", "Hahahah", 2, 231, 468)
  val records: Records = new Records(Seq(record1, record2))
  val page = example.html.invoice("007", records)
  println("A rendered Twirl HTML template:")
  private val htmlString: String = page.toString
  println(htmlString)

//  private val html: ByteArrayInputStream = new ByteArrayInputStream(htmlString.getBytes(StandardCharsets.UTF_8))
//
//  val body: String = Source.fromInputStream(html).mkString

  val pdf: Array[Byte] = {
    val renderer: PdfRenderer = new PdfRenderer(this.getClass.getClassLoader)
    renderer.toBytes(htmlString)
  }

  Files write (`test.pdf`, pdf)

  println(Json.toJson(records))

}
