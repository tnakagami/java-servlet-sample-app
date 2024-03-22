import app.sample.models;

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

  //! Enum's label name
  private String label;
  //! Enum's id
  private int id;

  /**
   * @brief Get Enum's label
   * @return String label
   */
  public String getLabel() {
    return label;
  }

  /**
   * @brief Get Enum's id
   * @return int id
   */
  public int getID() {
    return id;
  }

  /**
   * @brief Check input id
   * @param[in] id
   * @return boolean true  valid id
   * @return boolean false invalid id
   */
  public static boolean checkID(int id) {
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
   * @brief Get Role matching id
   * @param[in] id
   * @return Role result matched Role
   */
  public static Role getRole(int id) {
    Role result = Role.Viewer;

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