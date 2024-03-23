package app.sample.models;

import java.lang.ClassNotFoundException;
import java.util.Objects;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {
  // In the case of that the version of "mysql-connector-j" is "8.x"
  private final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
  private final String JDBC_CONNECTION = String.format("jdbc:mysql://%s:3306/%s", System.getenv("SERVLET_DATABASE_HOST"), System.getenv("MYSQL_DATABASE"));
  private final String USERNAME = System.getenv("MYSQL_USER");
  private final String PASSWORD = System.getenv("MYSQL_PASSWORD");
  private Connection connection = null;
  public static DatabaseHelper instance = null;

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
   * @brief Get records from database
   * @param[in] String query SQL statement
   * @return Resultset resultSet matched records
   */
  public ResultSet getRecords(String query) throws SQLException {
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);

    return resultSet;
  }

  /**
   * @brief Set records to database
   * @param[in] String query SQL statement
   * @return Resultset resultSet ids of matched records
   */
  public ResultSet setRecord(String query) throws SQLException {
    //! Assumption: The id has the attribute of AUTO_INCREMENT
    Statement statement = connection.createStatement();
    statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
    ResultSet resultSet = statement.getGeneratedKeys();

    return resultSet;
  }

  /**
   * @brief Get the DatabaseHelper's instance
   * @return DatabaseHelper instance DatabaseHelper's instance
   */
  public static DatabaseHelper getInstance() {
    if (Objects.isNull(instance)) {
      instance = new DatabaseHelper();
    }

    return instance;
  }
}