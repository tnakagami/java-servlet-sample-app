package app.sample.utils;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @brief Parse target url
 */
public class ParseUrl {
  private final int INVALID_RECORD = -1;
  private final int CREATE_RECORD = 1;
  private final int UPDATE_RECORD = 2;
  //! instance id
  private int id;
  //! url status
  private int state;

  /**
   * @brief constructor
   * @param[in] String path target url
   */
  public ParseUrl(String path) {
    id = INVALID_RECORD;
    state = INVALID_RECORD;

    if (Objects.isNull(path)) {
      state = CREATE_RECORD;
    }
    else {
      /**
       *  Collation conditions: 
       * 
       *  -# The 1st character is "/".
       *  -# After the 2nd character, the number must be at least one consecutive charactor.
       *  -# The last character ends with "/" (or not).
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
   * @brief Get id of this instance
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
  public boolean isInvalid() {
    return (INVALID_RECORD == state);
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