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

class UserValidator {
  private final int MAX_USERNAME_LEN = 255;
  //! errors
  private List<String> errors;
  //! validation functions
  private String[] validators;

  /**
   * @brief constructor
   */
  public UserValidator() {
    errors = new ArrayList<String>();
    validators = new String[] {"nameValidator", "roleValidator"};
  }

  /**
   * @brief Validate model's variables
   */
  protected void clean(User user) throws RuntimeException {
    Class<?> current = this.getClass();
    Class<?> target = user.getClass();

    for (String functionName: validators) {
      try {
        Method method = current.getDeclaredMethod(functionName, new Class[]{target});
        method.setAccessible(true);
        method.invoke(this, user);
      }
      catch (NoSuchMethodException|IllegalAccessException ex) {
        ex.printStackTrace();
      }
      catch (InvocationTargetException ex) {
        String err = ex.getCause().getMessage();
        setError(err);
      }
    }
    checkError();
  }

  /**
   * @brief Validate user id
   */
  protected void idValidator(User user) throws RuntimeException, IOException {
    //! Assumption: The function is only called in the case of updating the user information
    int id = user.getID();
    User own = user.getUser();
    //! Validation of user id
    if (Objects.isNull(own)) {
      throw new RuntimeException(String.format("Invalid user id (id: %d)", id));
    }
    //! In the case of valid user id
    user.setName(own.getName());
    user.setRole(own.getRole().getID());
  }

  /**
   * @brief Validate username
   */
  private void nameValidator(User user) throws RuntimeException {
    String name = user.getName();
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
  private void roleValidator(User user) throws RuntimeException {
    int roleID = user.getRoleID();
    //! Validation of user's role
    if (!Role.validID(roleID)) {
      throw new RuntimeException(String.format("Invalid user's role (id: %d)", roleID));
    }
    user.setRole(roleID);
  }

  /**
   * @breif Set error
   * @param[in] String err error message
   */
  protected void setError(String err) {
    errors.add(err);
  }

  /**
   * @brief Get errors
   * @return List<String> errors validation errors
   */
  protected List<String> getErrors() {
    return errors;
  }

  /**
   * @brief Check error
   */
  protected void checkError() throws RuntimeException {
    if (!errors.isEmpty()) {
      throw new RuntimeException();
    }
  }
}

public class User implements Serializable {
  private static final long serialVersionUID = 1L;
  //! user id
  private int id;
  //! username
  private String name;
  //! user's role
  private int roleID;
  private Role role;
  //! validator
  private UserValidator validator;
  //! In the case of updating record
  private boolean isUpdation;

  /**
   * @brief constructor
   */
  private User(boolean isUpdation) {
    id = 0;
    name = "";
    role = Role.getDefaultRole();
    roleID = role.getID();
    validator = new UserValidator();
    this.isUpdation = isUpdation;
  }

  /**
   * @brief Get errors
   * @return List<String> errors validation errors
   */
  public List<String> getErrors() {
    return validator.getErrors();
  }

  /**
   * @brief Check error
   */
  public void checkError() throws RuntimeException {
    validator.checkError();
  }

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
  public void setRole(String roleID) {
    try {
      this.roleID = Integer.parseInt(roleID);
    }
    catch (RuntimeException ex) {
      validator.setError("Failed to set role");
    }
  }

  /**
   * @brief Set role
   * @param[in] int role user's role id
   */
  protected void setRole(int role) {
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
   * @brief Get raw role id
   * @return int roleID
   */
  protected int getRoleID() {
    return roleID;
  }

  /**
   * @brief The function to get username
   * @param[in] int id user id
   * @return User user user's instance (may be null)
   */
  protected User getUser() throws IOException {
    List<User> users = getUsers(String.format("WHERE id = %d", id));
    User user = null;

    //! Extract username from the collected record
    if (users.size() > 0) {
      user = users.get(0);
    }

    return user;
  }

  /**
   * @brief The function to save model data to database
   */
  public void save() throws RuntimeException, SQLException, IOException {
    //! Validation
    validator.clean(this);

    try (QueryManager manager = new QueryManager()) {
      //! In the case of updating the user information
      if (isUpdation) {
        String sql = String.format("UPDATE User SET name='%s', role=%d WHERE id = %d ;", name, roleID, id);
        manager.execUpdate(sql);
      }
      //! In the case of creating the user
      else {
        String sql = String.format("INSERT INTO User (name, role) VALUES ('%s', %d) ;", name, roleID);
        manager.execUpdate(sql);
        //! Extract user's id from the inserted record
        if (manager.next()) {
          setID(manager.getInt(1));
        }
      }
    }
  }

  /**
   * @brief Get the instance of default user
   * @param[in] int id user id
   * @param[in] boolean isCreation (true: creating user, false: updating user information)
   * @return User user the instance of user
   */
  public static User getDefaultUser(int id, boolean isCreation) {
    boolean isUpdation = !isCreation;
    User user = new User(isUpdation);
    user.setID(id);

    //! In the case of updating the user information
    if (isUpdation) {
      try {
        user.validator.idValidator(user);
      }
      //! In the case of invalid user id
      catch (RuntimeException ex) {
        user.validator.setError(ex.getMessage());
      }
      catch (IOException ex) {
        user.validator.setError("Server Side Error");
      }
    }

    return user;
  }

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
        User user = new User(true);
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
