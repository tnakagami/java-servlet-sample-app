package app.sample.models;

import java.io.Serializable;
import java.lang.RuntimeException;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
//! local package
import app.sample.models.Role;

public class User implements Serializable {
  private static final long serialVersionUID = 1L;
  private final int MAX_USERNAME_LEN = 255;
  //! user id
  private int id;
  //! username
  private String name;
  //! user's role
  private Role role;
  //! helper instance
  private static DatabaseHelper helper = DatabaseHelper.getInstance();

  /**
   * @brief constructor
   * @param[in] String name username
   * @param[in] int role role id
   */
  public User(String name, int role) throws RuntimeException {
    //! Validation of username and user's role
    if (Objects.isNull(name)) {
      throw new RuntimeException("Invalid username: username is null.");
    }
    if ((0 == name.length()) || (name.length() > MAX_USERNAME_LEN)) {
      throw new RuntimeException(String.format("Invalid username's length (length: %d)", name.length()));
    }
    if (!Role.checkID(role)) {
      throw new RuntimeException(String.format("Invalid user's role (id: %d)", role));
    }
    id = 0;
    this.name = name;
    this.role = Role.getRole(role);
  }

  /**
   * @brief Get user id
   * @return int id
   */
  public int getID() {
    return id;
  }

  /**
   * @brief Get username
   * @return String name
   */
  public String getName() {
    return name;
  }

  /**
   * @brief Get user's role
   * @return Role role
   */
  public Role getRole() {
    return role;
  }

  /**
   * @brief The function to create user
   * @param[in] String name username
   * @param[in] int role user's role
   * @return User matched user's instance
   */
  public static User createUser(String name, int role) throws RuntimeException, SQLException {
    User user = new User(name, role);
    String sql = String.format("INSERT INTO User (name, role) values ('%s', %d) ;", user.name, user.role.getID());
    ResultSet resultSet = helper.setRecord(sql);
    //! Extract user's id from the inserted record
    if (resultSet.next()) {
      user.id = resultSet.getInt(1);
    }

    return user;
  }

  /**
   * @brief The function to update user's information
   * @param[in] int id user id
   * @param[in] String name username
   * @param[in] int role user's role
   * @return User matched user's instance
   */
  public static User updateUser(int id, String name, int role) throws RuntimeException, SQLException {
    User user = new User(name, role);
    String sql = String.format("UPDATE User SET name='%s', role=%d WHERE id = %d ;", user.name, user.role.getID(), id);
    helper.setRecord(sql);
    user.id = id;

    return user;
  }

  /**
   * @brief The function to get username
   * @param[in] int id user id
   * @return String name username (may be null)
   */
  public static String getName(int id) throws SQLException {
    String sql = String.format("SELECT name FROM User WHERE id = %d ;", id);
    ResultSet resultSet = helper.getRecords(sql);
    String name = null;

    //! Extract username from the collected record
    if (resultSet.next()) {
      name = resultSet.getString("name");
    }

    return name;
  }

  /**
   * @brief The function to collect the users registered to database
   * @param[in] String condition extracted condition
   * @return List<User> users list of the matched users
   */
  public static List<User> getUsers(String condition) {
    List<User> users = new ArrayList<User>();

    try {
      String sql = String.format("SELECT * FROM User %s ;", condition);
      ResultSet resultSet = helper.getRecords(sql);

      //! Convert the records to User's instances
      while (resultSet.next()) {
        //! Extract data from target record
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        int role = resultSet.getInt("role");
        //! Create User's instance
        User user = new User(name, role);
        user.id = id;
        //! Add instance to list
        users.add(user);
      }
    }
    catch(SQLException|RuntimeException  ex) {
      ex.printStackTrace();
    }

    return users;
  }
}