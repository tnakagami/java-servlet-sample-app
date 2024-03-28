package app.sample.models;

import java.util.List;
import java.util.ArrayList;

/**
 * @brief Role type
 */
public enum Role {
  //! Enum lists
  Admin("Admin", 1),
  Editor("Editor", 2),
  Viewer("Viewer", 3),
  ;

  //! role name
  private String label;
  //! role id
  private int id;

  /**
   * @brief constructor
   * @param[in] String label role name
   * @param[in] int id role id
   */
  private Role(String label, int id) {
    this.label = label;
    this.id = id;
  }

  /**
   * @brief Get role label
   * @return String label
   */
  public String getLabel() {
    return label;
  }

  /**
   * @brief Get role id
   * @return int id
   */
  public int getID() {
    return id;
  }

  /**
   * @brief Get default role
   * @return Role role
   */
  protected static Role getDefaultRole() {
    return Role.Viewer;
  }

  /**
   * @brief Check input id
   * @param[in] id
   * @return boolean true  valid id
   * @return boolean false invalid id
   */
  protected static boolean validID(int id) {
    boolean isValid = false;

    for (Role role: Role.values()) {
      if (role.getID() == id) {
        isValid = true;
        break;
      }
    }

    return isValid;
  }

  /**
   * @brief Get Role of matching id
   * @param[in] id
   * @return Role result matched Role
   */
  public static Role getRole(int id) {
    Role result = Role.getDefaultRole();

    for (Role role: Role.values()) {
      if (role.getID() == id) {
        result = role;
        break;
      }
    }

    return result;
  }

  /**
   * @brief Get valid roles
   * @return List<Role> roles valid roles
   */
  public static List<Role> getValidRoles() {
    List<Role> roles = new ArrayList<Role>();
    roles.add(Role.Editor);
    roles.add(Role.Viewer);

    return roles;
  }
}