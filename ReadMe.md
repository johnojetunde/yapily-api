# Yapily Marvel API

Yapily Marvel API.

### Dependencies

* Java 11
* maven

### Installing

* To build the artifact
* navigate to the root directory of the project and run the following command

```
mvn clean package
```

* You can run the artifact (yapily-api-0.0.1.jar)
* inject the required environmental variables at runtime or via application.properties

### Executing program

```
env MARVEL_PUBLIC_KEY=publicKeyValue MARVEL_PRIVATE_KEY=privateKeyValue java -jar target/yapily-api-0.0.1.jar
```

## Authors

[John Ojetunde](https://www.johnojetunde.com/)