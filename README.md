# GreenSky Java Chatroom
An amateur Java project that was made for exercise purposes during my study period as computer science student.
The project consists two parts, a **Multi-Threaded Java Server** and a simple **JavaFX Gui Client**.
It offers real time messaging room and basic authentication without storing logs.

## Needed Libraries
* Java11
* JavaFX
* jfoenix-9.0.10
* sqlite-jdbc-3.7.2 (your choice)

## Server Side
By default, Server runs on port 8080. It offers simple log messages 
about incoming connections and other client actions. Every new client is being handled on a separate thread by
```ClientHandler``` class.

## Client Side
Client offers a simple JavaFX gui with options to *Log in*, *Sign up*, *Send Messages* and *Log Out*. Take in mind
that a message from a client will be visible from all the other online clients! It's a single room.

### Run Client
In order to run Client you must specify also these VM options 
**--module-path  <```path to JavaFX sdk ```>  --add-modules javafx.controls,javafx.fxml**

### Sign Up 
In order to sign up for the first time client must provide a **Room Key**. Default key is "theboys". This can be changed
in ```ClientHandler``` class.

## Screenshots

### Server
![alt text](https://github.com/Panagiss/JavaFX-Server-Client-ChatRoom/blob/master/sample-images/Screenshot%20from%202020-12-14%2015-02-37.png)
![alt text](https://github.com/Panagiss/JavaFX-Server-Client-ChatRoom/blob/master/sample-images/Screenshot%20from%202020-12-14%2015-05-23.png)

### Client
![alt text](https://github.com/Panagiss/JavaFX-Server-Client-ChatRoom/blob/master/sample-images/Screenshot%20from%202020-12-14%2015-00-28.png)
![alt text](https://github.com/Panagiss/JavaFX-Server-Client-ChatRoom/blob/master/sample-images/Screenshot%20from%202020-12-14%2015-04-42.png)
![alt text](https://github.com/Panagiss/JavaFX-Server-Client-ChatRoom/blob/master/sample-images/Screenshot%20from%202020-12-14%2015-04-59.png)
