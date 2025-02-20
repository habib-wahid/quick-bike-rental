# Evolving a service from monolith to microservices with Axon Framework and Axon Server

The goal of this repo is to show how one can develop a well structured monolithic application that can evolve to become a set of microservices
using [Axon Framework and Axon Server](https://developer.axoniq.io/).

This starts as two services, Rental (Monolith) and Payment which work together to run the Axoniq World Wide Bike Rental Service. 
The Rental service manages the inventory and rental status of bikeCollection.  While the Payment service manages payment processing related to 
a bike rental.  

![Axoniq World Wide Bike Rental Architecture](/images/Bike-Rental-Quick-Start.monolith.png)

## Pre-requisites

The following software must be installed in your local environment:

* JDK version 21.

* Docker-Compose

## Quick Start

* An IDE such as [Jetbrains IDEA](https://www.jetbrains.com/idea/) is recommended.
### Start Services
Begin by running the `PaymentApplication` and `RentalApplication` Services in this order.  
This will start a docker image of Axon-Server using run the docker-compose.yaml file found in the root of the project. 
Once you have both services started you can see them connected to Axon-Server at http://localhost:8024/#overview
![Axon Server Overview](/images/Bike-Rental-Quick-Start-AxonServer-Overview.png)

From this page you are able to navigate to the details for each application by clicking on the application in the diagram.
Once on the details page for an application you are able to see the list of connected application instances, 
list of handled commands, list of handled queries, and running event processors


## Running our business
### Populate Inventory of Bikes
In order to begin offering our bike rental service we will need an inventory of bikeCollection.  To do this, navigate to the
[requests.http](./requests.http) file, find the section with the header ```### Generate bikeCollection``` and executing the http 
```POST``` command shown.  This will give you an inventory of bikeCollection which you can verify by executing the http command
found in the```### List all``` section of [requests.http](/requests.http) file.


### Generate Bike Rentals
Now that your inventory is in place it is time to make some money!!  To simulate all the steps of a rental and return 
(request a bike, completing payment, unlocking, and finally returning) we can execute the http command found at the header
```### Generate Rentals``` of the of [requests.http](/requests.http) file.


## Evolving Rental Application monolith to microservices
Great news!  The Axoniq World Wide Bike Rental Service is renting bikeCollection faster than we can buy them!  As a result our
Rental Application is experiencing some scalability issues.  To handle this increase in volume on our application it has
been determined that we need to break out the parts of the Rental Application each in to their own service.  Our updated
architecture now looks like the following...![Axoniq World Wide Bike Rental Microservices Architecture](/images/Bike-Rental-Quick-Start.microservices.png)

To make this happen run the [create-microservices.sh](create-microservices.sh) script to copy the necessary files into 
the pre-defined services in the project. Once the script is complete, you must stop the running `RentalApplication` app (port conflict),
and then run the new services `RentalCommandApplication`, `RentalPaymentSagaApplication`, `RentalQueryApplication`, and `UserInterfaceApplication`.

This allows us to run each aspect of our Rental domain as an independent service with no functional changes to the code base. 
Our initial approach of using features in Axon Framework such as Command Gateway and Query Gateway have provided us with
location independence between our components.   We are now able to evolve and scale each component as necessary to handle 
the increased load of our ever growing bike rental business.


## Monitoring our Axon Framework and Axon Server based services
To be able to understand the performance of our services, we can use the [Axoniq Console](https://console.axoniq.io). Using 
Axoniq Console we can register each of our microservices, check on performance command handling within our Aggregates (Bike and Payment),
query handling performance, and event processors as well. 
