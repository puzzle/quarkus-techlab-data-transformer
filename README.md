# data-transformer project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application

Run the application with live coding or for production.

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```s
./mvnw quarkus:dev
```

### Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `data-transformer-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/data-transformer-1.0-SNAPSHOT-runner.jar`.

#### Using a specific profile

There are two ways to set a custom profile, either via the `quarkus.profile` system property or the `QUARKUS_PROFILE` environment variable. If both are set the system property takes precedence.

To run the application in dev mode (profile), use `java -Dquarkus.profile=dev -jar target/data-consumer-1.0-SNAPSHOT-runner.jar`.

## Features and endpoints

Default configuration is to have a working rest data consumer. Kafka and Jaeger are not enabled.

### Basic features and endpoints

Health endpoint:
[APP_URL/health](http://0.0.0.0:8081/health)

Liveness endpoint:
[APP_URL/health/live](http://0.0.0.0:8081/health/live)

Readiness endpoint:
[APP_URL/health/ready](http://0.0.0.0:8081/health/ready)

Metrics (Prometheus):
[APP_URL/metrics](http://0.0.0.0:8081/metrics)

### Enable Kafka

Enable following configuration for the application.

```properties
# Configure the SmallRye Kafka connector
kafka.bootstrap.servers=amm-techlab-kafka-bootstrap:9092

# Configure the Kafka sink
mp.messaging.incoming.data.connector=smallrye-kafka
mp.messaging.incoming.data.topic=manual
mp.messaging.incoming.data.value.deserializer=ch.puzzle.quarkustechlab.reactivetransformer.control.SensorMeasurementDeserializer
```

Do it inside the [application.properties](src/main/resources/application.properties) file or by environment variables.

Example run command:

```s
./mvnw -Dkafka.bootstrap.servers=localhost:9092 \
  -Dmp.messaging.incoming.data.connector=smallrye-kafka \
  -Dmp.messaging.incoming.data.topic=manual \
  -Dmp.messaging.incoming.data.value.deserializer=ch.puzzle.quarkustechlab.reactivetransformer.control.SensorMeasurementDeserializer \
  clean quarkus:dev
```

### Enable OpenTracing / Jaeger

Settings to enable and configure Jaeger.

```properties
# Configure Jaeger
quarkus.jaeger.enabled=true
%dev.quarkus.jaeger.enabled=false
quarkus.jaeger.endpoint=http://jaeger-all-in-one-inmemory-collector:14268/api/traces
%dev.quarkus.jaeger.endpoint=http://localhost:14268/api/traces
quarkus.jaeger.service-name=data-transformer
quarkus.jaeger.sampler-type=const
quarkus.jaeger.sampler-param=1
quarkus.log.console.format=%d{HH:mm:ss} %-5p traceId=%X{traceId}, spanId=%X{spanId}, sampled=%X{sampled} [%c{2.}] (%t) %s%e%n
```

## App configuration by environment variables

To configure your application by environment variables, use the variable name from the *application.properties* file.
E.g. use `kafka.bootstrap.servers` to define the url of the bootstrap server.
The environment variables override the values of the *application.properties* file.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/data-transformer-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/building-native-image>.
