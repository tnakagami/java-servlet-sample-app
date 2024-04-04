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

public class QueryManager implements Closeable {
  // In the case of that the version of "mysql-connector-j" is "8.x"
  private final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
  private final String JDBC_CONNECTION = String.format("jdbc:mysql://%s:3306/%s", System.getenv("SERVLET_DATABASE_HOST"), System.getenv("MYSQL_DATABASE"));
  private Connection connection = null;
  private Statement statement = null;
  private ResultSet resultSet = null;

  /**
   * @brief constructor
   */
  protected QueryManager() throws SQLException {
    try {
      //! Connect to MySQL
      Class.forName(MYSQL_DRIVER);
      //! Connect to database
      String username = System.getenv("MYSQL_USER");
      String password = System.getenv("MYSQL_PASSWORD");
      connection = DriverManager.getConnection(JDBC_CONNECTION, username, password);
      statement = connection.createStatement();
    }
    catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
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

  //! Getters
  protected boolean next() throws SQLException {
    return resultSet.next();
  }
  protected int getInt(String name) throws SQLException {
    return resultSet.getInt(name);
  }
  protected int getInt(int idx) throws SQLException {
    return resultSet.getInt(idx);
  }
  protected String getString(String name) throws SQLException {
    return resultSet.getString(name);
  }
  protected boolean getBoolean(String name) throws SQLException {
    return resultSet.getBoolean(name);
  }

  /**
   * @brief Finalize statement and resultSet
   */
  @Override
  public void close() throws IOException {
    if (Objects.nonNull(resultSet)) {
      try {
        resultSet.close();
      }
      catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
    if (Objects.nonNull(statement)) {
      try {
        statement.close();
      }
      catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
    if (Objects.nonNull(connection)) {
      try {
        connection.close();
      }
      catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }
}