package app.sample.models;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.RuntimeException;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

public class User implements Serializable {
  private static final long serialVersionUID = 1L;
  private final int MAX_USERNAME_LEN = 255;
  //! user id
  private int id;
  //! username
  private String name;
  //! user's role
  private int roleID;
  private Role role;
  //! validation functions
  private String[] validators = new String[] {"nameValidator", "roleValidator"};

  // ========================
  // ===== Constructors =====
  // ========================
  /**
   * @brief constructor
   */
  public User() {
    id = 0;
    name = "";
    role = Role.getDefaultRole();
    roleID = role.getID();
  }

  /**
   * @brief constructor
   * @param[in] int id user id
   */
  public User(int id) throws RuntimeException, IOException {
    List<User> users = User.getUsers(String.format("WHERE id = %d", id));

    //! Extract username from the collected record
    if (users.size() > 0) {
      User user = users.get(0);
      this.id = user.getID();
      name = user.getName();
      role = user.getRole();
      roleID = role.getID();
    }
    else {
      throw new RuntimeException(String.format("Invalid user id (id: %d)", id));
    }
  }

  // ======================
  // ===== Validators =====
  // ======================
  /**
   * @brief Validate model's variables
   */
  public List<String> clean() {
    List<String> errors = new ArrayList<String>();
    Class<?> current = this.getClass();

    for (String functionName: validators) {
      try {
        Method method = current.getDeclaredMethod(functionName);
        method.setAccessible(true);
        method.invoke(this);
      }
      catch (NoSuchMethodException|IllegalAccessException ex) {
        ex.printStackTrace();
      }
      catch (InvocationTargetException ex) {
        String err = ex.getCause().getMessage();
        errors.add(err);
      }
    }

    return errors;
  }

  /**
   * @brief Validate username
   */
  private void nameValidator() throws RuntimeException {
    //! Validation of username
    if (Objects.isNull(name)) {
      throw new RuntimeException("Invalid username: username is null.");
    }
    if ((0 == name.length()) || (name.length() > MAX_USERNAME_LEN)) {
      throw new RuntimeException(String.format("Invalid username's length (length: %d)", name.length()));
    }
  }

  /**
   * @brief Validate user's role
   */
  private void roleValidator() throws RuntimeException {
    //! Validation of user's role
    if (!Role.validID(roleID)) {
      throw new RuntimeException(String.format("Invalid user's role (id: %d)", roleID));
    }
    setRole(roleID);
  }

  // ===============================
  // ===== Setters and Getters =====
  // ===============================
  /**
   * @brief Set user id
   * @param[in] String id user id
   */
  private void setID(int id) {
    this.id = id;
  }

  /**
   * @brief Get user id
   * @return int id
   */
  public int getID() {
    return id;
  }

  /**
   * @brief Set username
   * @param[in] String name username
   */
  public void setName(String name) {
    this.name = name.trim();
  }

  /**
   * @brief Get username
   * @return String name
   */
  public String getName() {
    return name;
  }

  /**
   * @brief Set role
   * @param[in] String roleID user's role id
   */
  public void setRole(String roleID) throws RuntimeException {
    try {
      this.roleID = Integer.parseInt(roleID);
    }
    catch (RuntimeException ex) {
      throw new RuntimeException("Failed to set role");
    }
  }

  /**
   * @brief Set role
   * @param[in] int role user's role id
   */
  private void setRole(int role) {
    this.role = Role.getRole(role);
    roleID = role;
  }

  /**
   * @brief Get user's role
   * @return Role role
   */
  public Role getRole() {
    return role;
  }

  /**
   * @brief The function to save model data to database
   * @param[in] boolean isCreation in the case of creating record
   */
  public void save(boolean isCreation) throws SQLException, IOException {
    try (QueryManager manager = new QueryManager()) {
      //! In the case of creating the user
      if (isCreation) {
        String sql = String.format("INSERT INTO User (name, role) VALUES ('%s', %d) ;", name, roleID);
        manager.execUpdate(sql);
        //! Extract user's id from the inserted record
        if (manager.next()) {
          setID(manager.getInt(1));
        }
      }
      //! In the case of updating the user information
      else {
        String sql = String.format("UPDATE User SET name='%s', role=%d WHERE id = %d ;", name, roleID, id);
        manager.execUpdate(sql);
      }
    }
  }

  // ============================
  // ===== Static functions =====
  // ============================
  /**
   * @brief The function to collect the users registered to database
   * @param[in] String condition extracted condition
   * @return List<User> users list of the matched users
   */
  public static List<User> getUsers(String condition) throws IOException {
    List<User> users = new ArrayList<User>();

    try (QueryManager manager = new QueryManager()) {
      String sql = String.format("SELECT * FROM User %s ;", condition);
      manager.execSelect(sql);

      //! Convert the records to User's instances
      while (manager.next()) {
        //! Create User's instance
        User user = new User();
        //! Extract data from target record
        user.setID(manager.getInt("id"));
        user.setName(manager.getString("name"));
        user.setRole(manager.getInt("role"));
        //! Add instance to list
        users.add(user);
      }
    }
    catch(SQLException|RuntimeException ex) {
      ex.printStackTrace();
    }

    return users;
  }
}
