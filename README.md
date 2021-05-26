# mobile-user-contact

[![Build Status](https://travis-ci.org/hmrc/mobile-user-contact.svg)](https://travis-ci.org/hmrc/mobile-user-contact) [ ![Download](https://api.bintray.com/packages/hmrc/releases/mobile-user-contact/images/download.svg) ](https://bintray.com/hmrc/releases/mobile-user-contact/_latestVersion)

The mobile-user-contact microservice provides an API that allows mobile app users to contact HMRC. It allows users to send feedback and to request support.

Currently this is implemented using Deskpro (via the `hmrc-deskpro` microservice).

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")

### Standard Responses

We use standard HTTP status codes to show whether an API request succeeded or not. They're usually:

|Status Range| Meaning |
|------------|--------|
|in the 200 to 299 range| if it succeeded; including code 202 if it was accepted by an API that needs to wait for further action|
|in the 400 to 499 range| if it didn't succeed because of a client error by your application|
|in the 500 to 599 range| if it didn't succeed because of an error on our server|

###  Submit Feedback
>This resource is user-restricted - it requires an Authorization header containing an OAuth 2.0 Bearer Token
```
POST /mobile-user-contact/feedback-submissions
```

Submits user feedback about the HMRC mobile app. Returns a 2xx response when the feedback submission was successfully added to the queue. May be enhanced in future to include a ticket URL or ID in the response.
##### Example Request
```
{
  "email": "testy@example.com",
  "message": "I think the app is great",
  "journeyId": "eaded345-4ccd-4c27-9285-cde938bd896d",
  "userAgent": "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)"
}
```

### Submit A Support Request (sometimes known as Get Help)
>This resource is user-restricted - it requires an Authorization header containing an OAuth 2.0 Bearer Token
```
POST /mobile-user-contact/support-requests

```

Sends a request for support. Returns a 2xx response when the feedback was successfully be added to the queue. May be enhanced in future to include a ticket URL or ID in the response.

##### Example Request
```
{
  "name": "Testy McTest",
  "email": "testy@example.com",
  "message": "Help!",
  "journeyId": "eaded345-4ccd-4c27-9285-cde938bd896d",
  "userAgent": "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
  "service": "mobile-help-to-save"
}
```