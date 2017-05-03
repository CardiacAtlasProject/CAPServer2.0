2016-07-17-AS:
* Created init project with Spring boot framework
* Used `spring-boot-starter-web` dependency in `pom.xml`
* Created `/hello` mapping for testing inside `App` class
* Added `spring-boot-devtools` dependency in `pom.xml`, which allows the server started inside Eclipse to automatically refreshed whenever you changed the program
* Added `fork` option true in `pom.xml` inside `spring-boot-maven-plugin`. In this way, you can run the server outside the Eclipse environment using:

   ```bash
   $ mvn spring-boot:start
   ```
   and the server is automatically refreshed when you modify the codes. Cool, eh?

   Stop it using:
   ```bash
   $ mvn spring-boot:stop
   ```

* Found a good cheat sheet for application.properties: [http://makerj.tistory.com/189](http://makerj.tistory.com/189)
* But the common one is here: [http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)
* Created application.properites.
* Server's context-path = `/xpacs-adm` with port number = 8585.
* Added log file in `application.properties`.
   How to log:

   ```java
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

   public class Foo {
     static final Logger logger = LoggerFactory.getLogger(Foo.class);

     public void doIt() {
       logger.debug("Did it again!");
       logger.info("just an info");
     }
   }
   ```

2016-08-03-AS:
* Create `dev` branch: this is for development.
* Adding [Spring Security](http://projects.spring.io/spring-security/):
  * Added dependency in the pom.xml

2016-09-22-AS:
* Refreshed from the start (not good layout)
* Two profiles provided in the maven pom.xml:
  * mysql (default)
  * h2 (can be used for testing later)
* To run a specific profile, call:

   ```bash
   $ mvn spring-boot:run -P mysql
   ```
* but you must specify the active profile in the application.properties file.
* There are specific profile properties:
  * ```devel```, which uses MySQL server on Local VM (my laptop).
  * ```captest```, which uses MySQL server on capsqltst01 (local UoA network).
  * ```capprod```, which uses MySQL server on capsqlprd01 (CAP's production server).

* Some useful notes from Spring when it runs the application:
  Application events are sent in the following order, as your application runs:
    1. An ```ApplicationStartedEvent``` is sent at the start of a run, but before any processing except the registration of listeners and initializers.
    2. An ```ApplicationEnvironmentPreparedEvent``` is sent when the Environment to be used in the context is known, but before the context is created.
    3. An ```ApplicationPreparedEvent``` is sent just before the refresh is started, but after bean definitions have been loaded.
    4. An ```ApplicationReadyEvent``` is sent after the refresh and any related callbacks have been processed to indicate the application is ready to service requests.
    5. An ```ApplicationFailedEvent``` is sent if there is an exception on startup.

2016-09-29-AS:
* In the application.properties, the `spring.jpa.hibernate.ddl-auto` property can be:
   * `none`  - don't do anything during startup
   * `create` - create necessary tables that are defined as `@Entity` classes. Drop existing ones.
   * `create-drop` - do the same as `create` but destroy when exiting.
   * `auto` - Spring will do `create-drop` if the platform database is memory, otherwise `none`.
   * `validate` - to check with the existing schema.
   * Do not use any of these values, but use `none`. We will supply our own schema and data script.
* Create `schema.sql` or `schema-{platform}.sql`, where platform is `mysql`, for initialize a database schema.
* Create `data.sql` or `schema-{platform}.sql` for populating initial data.
* Where to create these files?
    * Default is in the class root path, which is `src/main/resources`
    * Or we can create in other other folder and set the locations in the `spring.datasource.schema` and `spring.datasource.data` properties.
    * In this xpacs-adm application, I set `spring.datasource.schema=classpath:/sql/xpacs-create-schema.sql`, which the file `xpacs-create-schema.sql` is stored in ROOT:/src/main/resources/sql/ folder.
* If you want to prohibit database initialization, which is a good practice for production server, then set `spring.datasource.initialize=false`.
* Now the spring application won't run if the initialization fails. To disable that, set `spring.datasource.continue-on-error=true`.

2016-12-03-AS:
* Introducing vagrant:
   * Use bento/centos-6.7 box from Hashicrop's vagrant repo
   * Run: `vagrant up`
   * Two vm's are defined: web and db
   * After vagrant is up, call `vagrant ssh web` or `vagrant ssh db` to go to the vm
   * Provisioning with vagrant is with `vagrant/webserver-bootstrap.sh` for the web vm and `vagrant/dbserver-bootstrap.sh` for the db vm.
   * The config is `Vagrantfile` file
