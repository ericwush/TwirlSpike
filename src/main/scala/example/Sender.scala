package example

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.Properties
import javax.activation.DataHandler
import javax.mail.Message.RecipientType
import javax.mail.util.ByteArrayDataSource
import javax.mail.{Address, Session}
import javax.mail.internet.{MimeMultipart, MimeBodyPart, InternetAddress, MimeMessage}

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord
import com.amazonaws.services.s3.model.{S3Object, GetObjectRequest}
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient
import com.amazonaws.services.simpleemail.model._

class Sender {

  def email(s3Event: S3Event, context: Context): Unit = {
    if (s3Event == null || s3Event.getRecords.size() == 0) {
      return
    }
    println(s"s3Event size: ${s3Event.getRecords.size()}")
    println(s"FunctionName: ${context.getFunctionName}")
    val record: S3EventNotificationRecord = s3Event.getRecords.get(0)
    val srcBucket: String = record.getS3.getBucket.getName
    val srcKey: String = record.getS3.getObject.getKey
    val s3Client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider())
    val s3Object: S3Object = s3Client.getObject(new GetObjectRequest(srcBucket, srcKey))
    println(s"bucket: ${s3Object.getBucketName}")
    println(s"key: ${s3Object.getKey}")
    val toAddress: String = s3Object.getObjectMetadata.getUserMetadata.get("email")
    println(s"email: $toAddress")

    val session: Session = Session.getDefaultInstance(new Properties())
    val message: MimeMessage = new MimeMessage(session)
    message.setSubject("Your invoice", "UTF-8")
    message.setFrom(new InternetAddress("eric.wu@myob.com"))
    message.setRecipients(RecipientType.TO, toAddress)

    val multipart: MimeMultipart = new MimeMultipart()
    val attachment = new MimeBodyPart()
    attachment.setFileName("invoice.pdf")
    attachment.setDescription("An example pdf", "UTF-8")
    val dataSource: ByteArrayDataSource = new ByteArrayDataSource(s3Object.getObjectContent, "application/pdf")
    attachment.setDataHandler(new DataHandler(dataSource))

    multipart.addBodyPart(attachment)
    message.setContent(multipart)

    println("sending email...")

    val sesClient: AmazonSimpleEmailServiceClient = new AmazonSimpleEmailServiceClient(new EnvironmentVariableCredentialsProvider())
    val region: Region = Region.getRegion(Regions.US_WEST_2)
    sesClient.setRegion(region)
    val outputStream: ByteArrayOutputStream = new ByteArrayOutputStream()
    message.writeTo(outputStream)
    val rawMessage: RawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray))
    val rawEmailRequest: SendRawEmailRequest = new SendRawEmailRequest(rawMessage)
    sesClient.sendRawEmail(rawEmailRequest)
  }

}
