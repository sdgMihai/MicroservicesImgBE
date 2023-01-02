# Image filter

## Relevant information:
1.`oauth-authorization-server` is a Keycloak Authorization Server wrapped as a Spring Boot application.
2. There is one OAuth Client registered in the Authorization Server:
    1. Client Id: newClient
    2. Client secret: newClientSecret
    3. Redirect Uri: http://localhost:8089/
3. There are two OAuth Angular Front-end Apps:
    1. `oauth-ui-authorization-code-angular` - A simple Angular App
    2. `oauth-ui-authorization-code-angular-zuul` - An Angular App hosted as a Boot Application with embedded Zuul proxy
4. `oauth-resource-server` is a Spring Boot based RESTFul API, acting as a backend Application
5. There are two users registered in the Authorization Server:
    1. john@test.com / 123
    2. mike@other.com / pass
6. The module uses the new OAuth stack upgraded to Java 17.
7. Admin KEYCLOACK:
   1. bael-admin / pass
## Services
- Located in package service.
- The actual image processing takes place in ImgSrv, as this class, not only creates the necessary additional data
for multithreaded processing, but also has a nested class that implements Thread, called SubImageFilter.
- SubImageFilter calls the correct Filter class, as specified in process method of parent class.
- Each thread is given a slice of pixels rows from the image, and it operates

## Filters
- Located in filter package.
- Each filter implementation is created using Filter Factory and has an ```applyFilter``` method,
as specified by the Filter interface.
- Two filters are implemented, Black-and-white and sepia filters and the other are dummies,
to show the value of the FilterFactory.
- Double-threshold may need fine-tuning of thresholds, none done yet.

## Model
- The model class as well as DTO's are located in the model package.
- ImgBin is the model class, specifying the id of the image and the ```Binary``` representation.
- A successful upload of an image triggers the return of an answer withe ImageUploadResponse in the http body.


## Synchronization
- I used a mutex for gradient and double threshold filters.
- The service ImgSrv, canny-edge-detection, double threshold filter and gradient filter use the barrier.

## Internal representation
- In the utils package the classes used for the internal representation of the image are located along
those used to encapsulate thread specific data, called ThreadSpecificData and ThreadSpecificDataT.
- Class Image saved the image in a matrix of pixels, where each pixel is represented by the Pixel class.
- At Image creation, a border is automatically added, as some filters need a border for their 3x3 kernels.
- The Pixel class saves internally the red, green, blue and transparency or alpha.
- Both the application.properties and the log4j2.xml file contain settings regarding the logging, the application.properties \
contains the logging level(without this setting, nothing gets printed) and the other the log4j2 settings.

## Tests
- the test suite is a combination of unit tests and tests made to measure performance or simple tests.
This project was created by Mihai Gheoace.

## DOCS
- [why completablefuture ain't magically better then sync operations](https://www.baeldung.com/spring-mvc-async-vs-webflux)
