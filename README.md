# Dragons of Mugloar
by João Carlos (https://www.linkedin.com/in/joaocarlosvale/)

This project consists of an application that is able to achieve a score of at least 1000 points
 in Dragons of Mugloar Game(https://www.dragonsofmugloar.com/).

## Logic applied:
Try to execute the tasks sorted by Reward and Expiration priority. If the task is solved, your kind of probability 
is stored to be used in the next selection of tasks. Otherwise, the application avoid to use this probability.

## Technologies used:
* Java
* Spring
* Maven 

## Commands:

To generate JAR:

    mvn clean package

To run:

    java -jar target/dragonsOfMugloar-0.0.1.jar
    
To run tests:

    mvn test
