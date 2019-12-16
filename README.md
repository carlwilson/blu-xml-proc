BluBaker Export Processor
=========================

Getting Started
----------------

### Command Line Options
Typing `java -jar target/blu-xml-proc.jar --help` or
`java -jar target/blu-xml-proc.jar -h` at any stage will yield:

```bash
Processes a BluBaker eSafe export structure for SIP ingest.

usage: blu-xml-proc [flags] [DIRECTORY]
  -h | --help : Prints this message.
  -a | --analysis        : Analysis dry run only checks export integrity without processing.
  -f | --force           : Force processing of export even if analysis fails.
  [DIRECTORY]            : The root directory of the BluBaker export for processing.
```

#### Analyse an export
Checks:
- that all files in export can be found on file system (no overwrites)
- that parent can be traced back to hierarchy root
- generate checksum data?

#### F

Building and Testing
--------------------

### Requirements

- Java 8 >
- Maven 3

### Download, build from source and run
Using bash and git:
```bash
~/git$ git clone https://github.com/carlwilson/blu-xml-proc.git
~/git$ cd blu-xml-proc
~/git$ mvn clean package
~/git$ java -jar target/blu-xml-proc.jar --help
Processes a BluBaker eSafe export structure for SIP ingest.

usage: blu-xml-proc [flags] [DIRECTORY]
  -h | --help : Prints this message.
  -a | --analysis        : Analysis dry run only checks export integrity without processing.
  -f | --force           : Force processing of export even if analysis fails.
  [DIRECTORY]            : The root directory of the BluBaker export for processing.
```
