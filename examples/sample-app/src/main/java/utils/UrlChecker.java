package app.sample.utils;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @brief Url Checker
 */
public class UrlChecker {
  private final int INVALID_RECORD = -1;
  private final int CREATE_RECORD = 1;
  private final int UPDATE_RECORD = 2;
  //! target record's id
  private int id;
  //! url status
  private int state;

  /**
   * @brief constructor
   * @param[in] String path target url
   */
  public UrlChecker(String path) {
    id = INVALID_RECORD;
    state = INVALID_RECORD;

    //! In the case of the exact match
    if (Objects.isNull(path)) {
      state = CREATE_RECORD;
    }
    //! In the case of the partial match
    else {
      /**
       *  Collation conditions:
       *
       *  -# The 1st character is "/"
       *  -# After the 2nd character, the number must be at least one consecutive charactor
       *  -# The last character ends with "/" (or not)
       */
      Pattern pattern = Pattern.compile("^/([0-9]+)/?$");
      Matcher match = pattern.matcher(path);

      if (match.matches()) {
        id = Integer.parseInt(match.group(1));
        state = UPDATE_RECORD;
      }
    }
  }

  /**
   * @brief Get id of target record
   * @return int id
   */
  public int getID() {
    return id;
  }

  /**
   * @brief Get Url status
   * @return boolean true valid url
   * @return boolean false invalid url
   */
  public boolean isValid() {
    return (INVALID_RECORD != state);
  }

  /**
   * @brief Get creation url or not
   * @return boolean true creation url
   * @return boolean false not creation url
   */
  public boolean isCreation() {
    return (CREATE_RECORD == state);
  }
}
