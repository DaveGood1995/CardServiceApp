Card Services Application

Introduction
The specification provided for this technical test was to create a library and application that process
transactions on the PAYROC gateway. I was provided with the REST Api documentation along with some
sample Card Data, requests and responses. There was a stipulation that the application should 
handle all messaging and events from the library.

Table of Contents

Installation
Usage
Configuration
Library Documentation

Installation
The repo can be cloned as is, the app should run as normal when run on an android device. The library 
should also be pulled in and is already listed as a dependency for the app. The app uses a fragmented 
layout and calls different functions from the PaymentHandler library.

Breakdown of Payment Handler
PaymentHandler contains all models and communication functions between thr app and the library and 
between the library and the PAYROC gateway. I will quickly explain the main paymentHandler class.

Insert Card Transaction: The insertCardTransaction method formats a transaction string with the 
provided amount, order, and currency. This method is responsible for creating a transaction request
string but does not process the payment.

Make Payment: The makePayment method initiates a payment transaction by constructing a 
TransactionRequest object and passing it to a ServiceClient for processing. It returns a 
TransactionResponse object, which contains the result of the payment transaction.

Read Raw Resource as String: This method reads the content of a raw resource file specified by its 
resource ID and returns it as a string. This is used to read XML data from a resource file.

Hex String to Byte Array: It converts a hexadecimal string to a byte array. This is a utility 
function that is used internally for parsing card data.

Parse Cards: This method parses XML data containing card information and constructs a list of Card
objects. It uses DOM parsing to extract card details, including cardholder name, data KSN, 
serial number, encrypted data, payload type, and associated tags.

Get Random Card Data: It retrieves a random card's data from the SAMPLE_CARD data file.

Get TLV String: This method constructs a TLV (Tag-Length-Value) string from a selected card's tags. 
It takes a Card object as input and returns the TLV string, which is commonly used in payment 
processing.

Format Timestamp: It converts a timestamp string from one date format to another. This is useful
for formatting timestamps for display purposes.

The Service Client of Payment Handler contains all the communication functions for the PayRoc gateway,
mainly getting the Bearer token and making a payment request.

Get Bearer Token: This private method sends a GET request to the specified endpoint to obtain a 
bearer token for authentication. It adds the API key as an Authorization header and parses the 
response to extract the token. The token is used for authenticating subsequent requests to the API.

Make Payment: The makePayment method initiates a payment transaction by constructing a POST request 
with a JSON payload containing the transaction details. It includes the bearer token obtained from
the getBearerToken method in the request header for authentication. If the response indicates an 
error (e.g., 400 Bad Request, 401 Unauthorized), it throws custom exceptions based on the HTTP 
status codes.

Exception Handling: The class includes error handling for network-related exceptions and JSON 
parsing exceptions, providing meaningful custom exceptions with error messages to help identify and 
handle specific issues.

Resource Configuration: The class retrieves various configuration values from the Android 
application's resources, including the base URL, API endpoints, and API key, allowing for easy 
configuration and maintenance.
