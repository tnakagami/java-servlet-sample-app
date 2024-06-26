# [Java Servlet] sample-app
## Assumptions
In this document, the following two conditions are assumed to assure operation.

  1. The developers use the docker environment.
  1. The host environment is Raspberry Pi 3 model B of armv7l version (32bit).

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

Next, in the container (i.e. docker environment), execute the following command, where the `groupId` and `artifactId` mean package name and architecture name.

```bash
mvn archetype:generate -Duser.home=/var/maven -DgroupId=app.sample -DartifactId=sample-app -Dversion=1.0 -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false
```

| Item         | Detail            | Example      |
| :----        | :----             | :----        |
| `groupId`    | package name      | `app.sample` |
| `artifactId` | architecture name | `sample-app` |

The execution results of the above command are shown below.

```bash
# --- example of output ---
#
# [INFO] Scanning for projects...
# [INFO]
# [INFO] ------------------< org.apache.maven:standalone-pom >-------------------
# [INFO] Building Maven Stub Project (No POM) 1
# [INFO] --------------------------------[ pom ]---------------------------------
# [INFO]
# [INFO] >>> archetype:3.2.1:generate (default-cli) > generate-sources @ standalone-pom >>>
# [INFO]
# [INFO] <<< archetype:3.2.1:generate (default-cli) < generate-sources @ standalone-pom <<<
# [INFO]
# [INFO]
# [INFO] --- archetype:3.2.1:generate (default-cli) @ standalone-pom ---
# [INFO] Generating project in Batch mode
# [INFO] ----------------------------------------------------------------------------
# [INFO] Using following parameters for creating project from Old (1.x) Archetype: maven-archetype-webapp:1.0
# [INFO] ----------------------------------------------------------------------------
# [INFO] Parameter: basedir, Value: /var/maven/app
# [INFO] Parameter: package, Value: app.sample
# [INFO] Parameter: groupId, Value: app.sample
# [INFO] Parameter: artifactId, Value: sample-app
# [INFO] Parameter: packageName, Value: app.sample
# [INFO] Parameter: version, Value: 1.0
# [INFO] project created from Old (1.x) Archetype in dir: /var/maven/app/sample-app
# [INFO] ------------------------------------------------------------------------
# [INFO] BUILD SUCCESS
# [INFO] ------------------------------------------------------------------------
# [INFO] Total time:  55.555 s
# [INFO] Finished at: 2000-03-31T11:11:11Z
# [INFO] ------------------------------------------------------------------------
#
# --- end of output ---
```

Then, to come back to the host environment, type `exit` or press `Ctrl + D`.

### Step4: Setup the project to use maven
In the host environment, enter the following commands in your terminal to change directory and create directories.

```bash
cd maven/project/sample-app
mkdir -p src/main/java
```

Next, open `pom.xml` file on your favorite editor (`vim pom.xml` in my case) and add these sentences between `<dependencies>` and `</dependencies>`.

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
      <plugin>
        <groupId>io.leonard.maven.plugins</groupId>
        <artifactId>jspc-maven-plugin</artifactId>
        <version>4.2.0</version>
        <executions>
          <execution>
            <id>jspc</id>
            <goals>
              <goal>compile</goal>
            </goals>
            <configuration />
          </execution>
        </executions>
        <dependencies>
          <dependency>
            <groupId>org.apache.tomcat</groupId>
            <artifactId>tomcat-jasper</artifactId>
            <version>10.1.18</version>
            <exclusions>
              <exclusion>
                <groupId>org.eclipse.jdt</groupId>
                <artifactId>ecj</artifactId>
              </exclusion>
            </exclusions>
          </dependency>
          <dependency>
            <groupId>org.eclipse.jdt</groupId>
            <artifactId>ecj</artifactId>
            <version>3.36.0</version>
          </dependency>
        </dependencies>
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
# In the case of having compiled the sample-app
cp -f maven/project/sample-app/target/sample-app.war servlet/webapps
```

## Execution
Execute the following commands to start servlet-server and database-server.

```bash
# To destroy the old containers
docker-compose down
# To create the containers
docker-compose up -d
```

Then, access to `http://server-ip-addres:16384/artifactId` to check the operation of your web application.

```bash
# For example, in the case of sample-app launched on a server whose IP address is 192.168.0.2
http://192.168.0.2:16384/sample-app
```