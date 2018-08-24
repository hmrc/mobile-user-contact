package uk.gov.hmrc.mobileusercontact.test

object IntegrationTestJson {

  val feedbackSubmissionJson: String =
    """
      |{
      |  "email": "testy@example.com",
      |  "message": "I think the app is great",
      |  "signUpForResearch": true,
      |  "town": "Leeds",
      |  "journeyId": "eaded345-4ccd-4c27-9285-cde938bd896d",
      |  "userAgent": "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)"
      |}
    """.stripMargin

  val supportRequestJson: String =
    """
      |{
      |  "name": "John Smith",
      |  "email": "testy@example.com",
      |  "message": "I can't find my latest payment",
      |  "journeyId": "eaded345-4ccd-4c27-9285-cde938bd896d",
      |  "userAgent": "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
      |  "service": "HTS"
      |}
    """.stripMargin
}
