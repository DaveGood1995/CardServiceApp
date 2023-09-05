# Card Services Application

## Introduction
The specification provided for this technical test was to create a library and application that process
transactions on the PAYROC gateway. I was provided with the REST API documentation along with some
sample Card Data, requests, and responses There was a stipulation that the application should
handle all messaging and events from the library.

## Installation
The repo can be cloned as is, and the app should build and run as normal when loaded in Android Studio. The library 
is called PaymentHandler and will be included in the cloned repo. It is already listed as a dependency for the app. The app uses a fragmented 
layout and calls different functions from the PaymentHandler library.

## Payment Handler Library

**Payment Handler Class**</br>
PaymentHandler contains all models and communication functions between the app and the library, as well as
between the library and the PAYROC gateway. I will quickly give a breakdown of the paymentHandler class.

**Insert Card Transaction:**</br>
The insertCardTransaction method formats a transaction string with the
provided amount, order, and currency. This method is responsible for creating a transaction request
string but does not process the payment. The string is returned to the app to be displayed in a dialog.

**Make Payment:**</br>
The makePayment method initiates a payment transaction by constructing a 
TransactionRequest object and passing it to a ServiceClient for processing. It returns a 
TransactionResponse object, which contains the result of the payment transaction.

**Read Raw Resource as String:**</br>
This method reads the content of a raw resource file specified by its 
resource ID and returns it as a string. This is used to read XML data from a resource file.

**Hex String to Byte Array:**</br>
It converts a hexadecimal string to a byte array. This is a utility 
function that is used internally for parsing card data.

**Parse Cards:**</br>
This method parses XML data containing card information and constructs a list of Card
objects. It uses DOM parsing to extract card details, including cardholder name, data KSN, 
serial number, encrypted data, payload type, and associated tags.

**Get Random Card Data:**</br>
It retrieves a random card's data from the SAMPLE_CARD data file.

**Get TLV String:**</br> 
This method constructs a TLV (Tag-Length-Value) string from a selected card's tags. 
It takes a Card object as input and returns the TLV string, which is commonly used in payment 
processing.

**Format Timestamp:**</br> 
It converts a timestamp string from one date format to another. This is useful
for formatting timestamps for display purposes.

### Service Client
The Service Client of Payment Handler contains all the communication functions for the PayRoc gateway,
mainly getting the Bearer token and making a payment request.

**Get Bearer Token:**</br> 
This private method sends a GET request to the specified endpoint to obtain a 
bearer token for authentication. It adds the API key as an Authorization header and parses the 
response to extract the token. The token is used for authenticating subsequent requests to the API.

**Make Payment:**</br> 
The makePayment method initiates a payment transaction by constructing a POST request 
with a JSON payload containing the transaction details. It includes the bearer token obtained from
the getBearerToken method in the request header for authentication. If the response indicates an 
error (e.g., 400 Bad Request, 401 Unauthorized), it throws custom exceptions based on the HTTP 
status codes.

**Exception Handling:**</br> 
The class includes error handling for network-related exceptions and JSON 
parsing exceptions, providing meaningful custom exceptions with error messages to help identify and 
handle specific issues.

**Resource Configuration:**</br> 
The class retrieves various configuration values from the Android 
application's resources, including the base URL, API endpoints, and API key, allowing for easy 
configuration and maintenance.

### Receipt Database Handler
The ReceiptDatabaseHandler serves as a helper for managing a SQLite database that stores receipts. It provides 
methods to perform common database operations such as insertion, retrieval, deletion, and checking for the 
existence of receipts.

**Database Creation and Upgrade:**</br>
The onCreate method sets up the database by executing an SQL command to create a table for storing receipt data.
The onUpgrade method handles database schema upgrades by deleting the existing table and recreating it with a 
new structure when the database version changes.

**Insertion (insertReceipt method):**</br>
Inserts a new receipt into the database, converting it to ContentValues.Uses Gson to serialize the TransactionResponse
object to JSON before insertion.

**Retrieval by ID (getReceiptById method):**</br>
Retrieves a receipt by its unique ID from the database and returns it. Uses Gson to deserialize the stored
JSON back into a TransactionResponse object.

**Deletion by ID (deleteReceipt method):**</br>
Deletes a receipt from the database by its unique ID.

**Has Receipts:**</br>
Checks if there are any receipts stored in the database.

**Get All Receipts:**</br>
Retrieves all receipts from the database and returns them as a list of pairs, where each pair consists of the receipt's 
ID and the receipt itself.

### HTTPExceptionHandler
Custom Exception class to handle different error types that can b presented by the PAYROC Gateway.

### Models
I set up different data models to both formulate data in the structure required for the PAYROC Gateway and also to make it easier to
correctly format and access certain data elements when required.

### Testing
There are some unit tests setup to test functions of the library that are called by the App, to ensure core functionality isnt broken when 
further development occurs. 

## Payment Processor App
Payment Processor is a relatively simple app put together to interact with and demonstrate the functionality of the PaymentHandler Library. 
It uses Fragments for UI elements and popup dialogs for system messages. 

### Home Fragment
This initial loading screen for the app, it is the main hub. From here you can navigate to Make a Payment, View stored Receipts - if there are
any present and view App Help screen.


![Initial Launch](https://github.com/DaveGood1995/CardServiceApp/assets/52549556/331070d7-20ea-4f9f-a041-c7f896b5f869)
![Home with Receipts](https://github.com/DaveGood1995/CardServiceApp/assets/52549556/89d288a2-83b5-43b0-a7d2-9fe364249121)

### Payment Fragment
This is the main processing fragment of the app. The user can either enter an OrderId or generate a random one using the button. The user must also
enter an amount, there is some basic validation to ensure entries will not cause errors. Once a transaction is started, the app contacts the library
and gets back an insert card message. Delays have been added to slow the process down, after 5 seconds the library grabs a random card from our Sample_Card
data and returns it to the app, after another 5 seconds the app triggers the MakePayment class which calls the library, populates TransactionRequest with required
data and sends the request to the library in a coroutine that is tied to the fragments lifecycle. 

The app will account for different card types and create the request as required. Errors will be handled gracefully and should give the user some indication as to 
what went wrong, with an error code they can use to guide the developer to help fix their specific problem. Once a trasnaction is successfully completed
the user will be passed to the Receipt Detail Fragment to view their transaction, this new receipt will also be stored in the database for review later. 


![path](https://github.com/DaveGood1995/CardServiceApp/assets/52549556/fe63f3dc-f5b0-4efe-a9d2-78a753471d32)


### Receipt List Fragment
Displays a list of stored receipts from the database of previous transactions, all items are clickable and will take you to Receipt Detail Fragment.

![Receipt List](https://github.com/DaveGood1995/CardServiceApp/assets/52549556/8694a534-ffc0-406e-9d8c-8da564486dbc)

### Receipt Detail Fragment
Loads and shows the chosen Receipt from Receipt List fragment, presents the receipt in a view. There is an option to delete the open receipt and 
another to return to Home Fragment.


![Receipt Example](https://github.com/DaveGood1995/CardServiceApp/assets/52549556/2cb41433-1da2-4af6-bc98-f00989708a32)
![Delete Receipt](https://github.com/DaveGood1995/CardServiceApp/assets/52549556/e157c849-29ea-4570-98bf-9639054ff6ee)

### Receipt List Adapter
Takes Order Id, Timestamp and Amount from all entries in the database and lays out all Receipts using Receipt_item view and returns the elements to be 
populate the RecyclerView.

### App Help Fragment
Gives the current app version, a brief description of the app and a button to Contact the Developer. When clicked this will auto fill an email to the developer
with a templated email with details to fill out in the users preferred email app. 


![App Help](https://github.com/DaveGood1995/CardServiceApp/assets/52549556/fc8d9671-f547-42ed-bb10-0f55a0b43739)
![Contact Email](https://github.com/DaveGood1995/CardServiceApp/assets/52549556/58169ea7-a759-4d01-9d8a-67c0bf7a70c0)

## With more time...
If I had some more time:
I would publish the library independently.
I would make the MakePayment method in the Payment Fragment more readable by splitting some of the functionality into separate functions. 
I would add more functionality using the PAYROC gateway.
