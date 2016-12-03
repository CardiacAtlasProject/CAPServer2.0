# Development

* Java SDK 1.8
* Eclipse Neon
* Maven 3.3.9
* Spring framework
* JOOQ
* MySQL database connection
* Embedded Tomcat server, currently Apache Tomcat/8.0.36

# Quick start

In the root project folder:

```bash
$ mvn dependency:tree
$ mvn spring-boot:run
```

Open [http://localhost:8585](http://localhost:8585).

If you want to create start/stop service, instead of run, you can call `mvn spring-boot:start` and `mvn spring:boot-stop`.

# Packaging to JAR

In the root project folder:

```bash
$ mvn package
$ java -jar target/[OUTPUT_JAR_FILE].jar
```

Open [http://localhost:8585](http://localhost:8585).

# Import to Eclipse

In the root project folder:

```bash
$ mvn eclipse:eclipse
```

* Open Eclipse -> Import Maven project.
* Run application

Open [http://localhost:8585](http://localhost:8585).
