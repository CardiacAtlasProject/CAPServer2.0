# Development

* Java SDK 1.8
* Maven 3.3.9 for dependency management
* [Spring boot 1.4.2](https://projects.spring.io/spring-boot/), for the best Java web framework
* MySQL database connection
* Embedded Tomcat server, currently Apache Tomcat/8.0.36, for the backend web server
* [Thymeleaf 3.0.2](http://www.thymeleaf.org/) for the web viewer
* [Layout 2.1.1](https://github.com/ultraq/thymeleaf-layout-dialect) for Thymeleaf template reusable
* [Bootstrap 3.3.7](http://getbootstrap.com/) for UI layout
* [Lombok 1.16.4](https://projectlombok.org/) for auto setter/getter

# Quick start

In the root project folder:

```bash
$ mvn dependency:tree
$ ./run.sh
```

Open [http://localhost:8585/xpacs-web](http://localhost:8585/xpacs-web).

If you want to create start/stop service, instead of run, you can call `mvn spring-boot:start` and `mvn spring:boot-stop`.

# Packaging to JAR

In the root project folder:

```bash
$ mvn package
$ java -jar target/[OUTPUT_JAR_FILE].jar
```

# Import to Eclipse

In the root project folder:

```bash
$ mvn eclipse:eclipse
```

* Open Eclipse -> Import Maven project.
* Run application
