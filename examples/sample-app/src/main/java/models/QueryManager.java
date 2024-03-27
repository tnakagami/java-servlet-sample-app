package app.sample.models;

import java.io.IOException;
import java.io.Closeable;
import java.lang.AutoCloseable;
import java.lang.ClassNotFoundException;
import java.util.Objects;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

class DatabaseHelper {
  // In the case of that the version of "mysql-connector-j" is "8.x"
  private final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
  private final String JDBC_CONNECTION = String.format("jdbc:mysql://%s:3306/%s", System.getenv("SERVLET_DATABASE_HOST"), System.getenv("MYSQL_DATABASE"));
  private final String USERNAME = System.getenv("MYSQL_USER");
  private final String PASSWORD = System.getenv("MYSQL_PASSWORD");
  private Connection connection = null;
  static DatabaseHelper instance = null;

  /**
   * @brief constructor
   */
  private DatabaseHelper() {
    try {
      //! Connect to MySQL
      Class.forName(MYSQL_DRIVER);
      //! Connect to database
      connection = DriverManager.getConnection(JDBC_CONNECTION, USERNAME, PASSWORD);
    }
    catch (ClassNotFoundException|SQLException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * @brief Get statement
   * @return Statement statement SQL statement
   */
  protected Statement getStatement() throws SQLException {
    return connection.createStatement();
  }

  /**
   * @brief Get the DatabaseHelper's instance
   * @return DatabaseHelper instance DatabaseHelper's instance
   */
  static DatabaseHelper getInstance() {
    if (Objects.isNull(instance)) {
      instance = new DatabaseHelper();
    }

    return instance;
  }
}

public class QueryManager implements Closeable {
  private Statement statement;
  private ResultSet resultSet;
    //! helper instance
  private static DatabaseHelper helper = DatabaseHelper.getInstance();

  /**
   * @brief constructor
   */
  protected QueryManager() throws SQLException {
    statement = helper.getStatement();
    resultSet = null;
  }

  /**
   * @brief Get records from database
   * @param[in] String query SQL statement
   * @return SqlHelper helper helper function of matched records
   */
  protected void execSelect(String query) throws SQLException {
    resultSet = statement.executeQuery(query);
  }

  /**
   * @brief Set records to database
   * @param[in] String query SQL statement
   */
  protected void execUpdate(String query) throws SQLException {
    //! Assumption: The id has the attribute of AUTO_INCREMENT
    statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
    resultSet = statement.getGeneratedKeys();
  }

  //! Checker
  protected boolean next() throws SQLException {
    return resultSet.next();
  }
  //! Getters
  protected int getInt(String name) throws SQLException {
    return resultSet.getInt(name);
  }
  protected int getInt(int idx) throws SQLException {
    return resultSet.getInt(idx);
  }
  protected String getString(String name) throws SQLException {
    return resultSet.getString(name);
  }

  /**
   * @brief Finalize statement and resultSet
   */
  @Override
  public void close() throws IOException {
    try {
      if (Objects.nonNull(resultSet)) {
        resultSet.close();
      }
      statement.close();
    }
    catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}