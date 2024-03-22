# Java Servlet Sample app
## Assumptions
In this application, I assume the two conditions.

  1. The developers use the docker environment.
  1. The host environment is Raspberry Pi 4 of armv7l version (32bit).

      Note: Please enter the command `uname -a` on your terminal to check kernel version.

## Preparations
### Step1: Create `.env` file in the `database` directory
Please check the [README.md](./database/README.md) for detail.

### Step2: Build images
First, run the following command to create docker images.

```bash
# Current directory: java-servlet-sample-app
docker-compose build
```

### Step3: Create a project (first time only)
In the host environment, enter the following command in your terminal.

```bash
docker-compose run --rm maven-server /bin/bash
```

Next, in the container (at docker environment), execute the following commands, where the `groupId` and `artifactId` mean package name and architecture name.

Also, you will be able to access to `http://server-ip-address:16384/sample-app` by the time you finish reading this README.md.

```bash
mvn archetype:generate -Duser.home=/var/maven -DgroupId=app.sample -DartifactId=sample-app -Dversion=1.0 -DarchetypeArtifactId=maven-archetype-webapp
```

| Item         | Detail            | Example      |
| :----        | :----             | :----        |
| `groupId`    | package name      | `app.sample` |
| `artifactId` | architecture name | `sample-app` |

The execution results of the above command are shown below.

```bash
# --- example of output ---
#   [INFO] Using property: groupId = app.sample
#   [INFO] Using property: artifactId = sample-app
#   [INFO] Using property: version = 1.0
#   [INFO] Using property: package = app.sample
#   Confirm properties configuration:
#   groupId: app.sample
#   artifactId: sample-app
#   version: 1.0
#   package: app.sample
#    Y: :

# *** Press the Enter key ***

#   [INFO] ----------------------------------------------------------------------------
#   [INFO] Using following parameters for creating project from Old (1.x) Archetype: maven-archetype-webapp:1.0
#   [INFO] ----------------------------------------------------------------------------
#   [INFO] Parameter: basedir, Value: /var/maven/app
#   [INFO] Parameter: package, Value: sample.app
#   [INFO] Parameter: groupId, Value: sample.app
#   [INFO] Parameter: artifactId, Value: sample-app
#   [INFO] Parameter: packageName, Value: sample.app
#   [INFO] Parameter: version, Value: 1.0
#   [INFO] project created from Old (1.x) Archetype in dir: /var/maven/app/sample-app
#   [INFO] ------------------------------------------------------------------------
#   [INFO] BUILD SUCCESS
#   [INFO] ------------------------------------------------------------------------
#   [INFO] Total time:  55.555 s
#   [INFO] Finished at: 2000-03-31T11:11:11Z
#   [INFO] ------------------------------------------------------------------------
# --- end of output ---
```

Then, to come back to the host environment, type `exit` or press `Ctrl + D`.

### Step4: Setup the project to use maven
In the host environment, to change directory and create directories, enter the following commands in your terminal.

```bash
cd maven/project/sample-app
mkdir -p src/main/java
```

Next, open XML file of `pom.xml` on your favorite editor. (In my case, I will use "vim".)

Then, add these sentences between `<dependencies>` and `</dependencies>`.

```xml
    <!-- https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api -->
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-api</artifactId>
      <version>5.10.0</version>
      <scope>test</scope>
    </dependency>
    <!-- https://mvnrepository.com/artifact/jakarta.servlet/jakarta.servlet-api -->
    <dependency>
      <groupId>jakarta.servlet</groupId>
      <artifactId>jakarta.servlet-api</artifactId>
      <version>6.0.0</version>
      <scope>provided</scope>
    </dependency>
    <!-- https://mvnrepository.com/artifact/jakarta.servlet.jsp/jakarta.servlet.jsp-api -->
    <dependency>
      <groupId>jakarta.servlet.jsp</groupId>
      <artifactId>jakarta.servlet.jsp-api</artifactId>
      <version>3.1.1</version>
      <scope>provided</scope>
    </dependency>
    <!-- https://mvnrepository.com/artifact/jakarta.servlet.jsp.jstl/jakarta.servlet.jsp.jstl-api -->
    <dependency>
        <groupId>jakarta.servlet.jsp.jstl</groupId>
        <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
        <version>3.0.0</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.taglibs/taglibs-standard-impl -->
    <dependency>
        <groupId>org.apache.taglibs</groupId>
        <artifactId>taglibs-standard-impl</artifactId>
        <version>1.2.5</version>
        <scope>runtime</scope>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.taglibs/taglibs-standard-jstlel -->
    <dependency>
        <groupId>org.apache.taglibs</groupId>
        <artifactId>taglibs-standard-jstlel</artifactId>
        <version>1.2.5</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.taglibs/taglibs-standard-spec -->
    <dependency>
        <groupId>org.apache.taglibs</groupId>
        <artifactId>taglibs-standard-spec</artifactId>
        <version>1.2.5</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.glassfish.web/jakarta.servlet.jsp.jstl -->
    <dependency>
        <groupId>org.glassfish.web</groupId>
        <artifactId>jakarta.servlet.jsp.jstl</artifactId>
        <version>3.0.1</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/com.mysql/mysql-connector-j -->
    <dependency>
      <groupId>com.mysql</groupId>
      <artifactId>mysql-connector-j</artifactId>
      <version>8.1.0</version>
    </dependency>
```

Also add these following sentences between `<build>` and `</build>`.

```xml
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
          <source>17</source>
          <target>17</target>
        </configuration>
      </plugin>
    </plugins>
```

### Step5: Develop your web applications by using Java Servlet
In the `maven/project/sample-app/src/main` directory, store Java's source files in the `java` directory and JSP (Java Servlet Pages) files in the `webapp` directory.

Sample codes are stored in the [examples](./examples) directory.

### Step6: Compile source codes
Run the following command.

```bash
docker-compose run --rm maven-server /bin/bash compile.sh
```

Then, copy `*.war` file to `servlet/webapps`. For details, see the following command.

```bash
cp -f maven/project/sample-app/target/sample-app.war servlet/webapps
```

Finally, modify the `docker-compose.yml` at line 22-23.

```yml
# *** before ***
    #volumes:
    #  - ./servlet/webapps/sample.war:/usr/local/tomcat/webapps/sample.war

# *** after ***
    volumes:
      - ./servlet/webapps/sample.war:/usr/local/tomcat/webapps/sample.war
```

## Execution
Execute the following command to start tomcat server and database server.

```bash
docker-compose up -d
```

Then, access to `http://server-ip-addres:16384/sample-app` to check your web application.
