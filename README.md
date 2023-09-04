# tiny-socks5

A simple Socks5 implementation in Java utilizing the standard library.

### Prerequisites

* Java installed on your machine. The program was written with JDK 17.
* A terminal or command prompt for running the program.

### Installation

```
git clone https://github.com/stanislaw-am-i/tiny-socks5.git
cd tiny-socks5
javac -d build src/main/java/Main.java
```

The compiled .class files will be generated in the build directory. Create the JAR file using the jar command:

```
jar cvf tiny-socks5.jar -C build .
```
### Command Line Arguments
The program accepts the following command line arguments:

    -a, --auth <arg>: Set authentication credentials in the format 'username:password'.
    -h, --help: Display this help message.
    -p, --port <arg>: Set the port for the server to listen on.

### Running the Program
To run the program with authentication credentials and a custom port, use the -a and -p options:
```
java -jar tiny-socks5.jar -a your_username:your_password -p 8080
```
If you don't specify authentication credentials, the program will run without authentication:
```
java -jar tiny-socks5.jar -p 8080
```
Replace `8080` with the port number you want to use.
