# scratchGame

## Description
`scratchGame` is a Java-based application that simulates a scratch card game. The game takes a configuration file and a betting amount as input, processes the game logic, and outputs the result in JSON format.

## Prerequisites
- Java 21 or higher
- Maven 3.9.9 or higher

## Build Instructions
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd scratchGame

2. Build the project 
   mvn clean install
3. Run the Project
   You can run this from your IDE by using Scratch class, But you have to pass cmd arguments like this --config <config-file-path> --betting-amount <amount>

    OR
   You Can package this into jar file by using
   mvn clean package
   it will generate a jar file in the target folder and you can run that jar file using
   java -jar target/scratchGame-1.0-SNAPSHOT.jar --config <config-file-path>    --betting-amount <amount> 
