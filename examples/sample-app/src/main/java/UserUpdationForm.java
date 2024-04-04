package app.sample;

import java.io.IOException;
import java.lang.RuntimeException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//! local package
import app.sample.forms.UserModelForm;

@WebServlet(name="UserUpdationForm", urlPatterns={"/user/*"})
public class UserUpdationForm extends HttpServlet {
  Pattern pattern = Pattern.compile("^/([0-9]+)/?$");

  private int getInstanceID(String path) throws RuntimeException {
    /**
     *  Collation conditions:
     *
     *  -# The 1st character is "/"
     *  -# After the 2nd character, the number must be at least one consecutive charactor
     *  -# The last character ends with "/" (or not)
     */
    Matcher match = pattern.matcher(path);
    int id;

    try {
      match.matches();
      id = Integer.parseInt(match.group(1));
    }
    catch (Exception ex) {
      throw new RuntimeException("Bad Request");
    }

    return id;
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html;charset=UTF-8");

    try {
      int id = getInstanceID(request.getPathInfo());
      UserModelForm form = new UserModelForm(id);
      form.executeResponseProcess(request, response, this.getServletContext());
    }
    //! In the case of invalid user id
    catch (RuntimeException ex) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html;charset=UTF-8");

    try {
      int id = getInstanceID(request.getPathInfo());
      UserModelForm form = new UserModelForm(id);
      form.getParameter(request);

      //! In the case of the validation process is success
      if (form.valid()) {
        try {
          //! Update model data to Database
          form.save();
          //! Page transition process
          response.sendRedirect(request.getContextPath());
        }
        //! In the case of SQL exception
        catch (SQLException ex) {
          String err = "Occur an error inside the HTTP server which execute the SQL statement.";
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, err);
        }
      }
      //! In the case of the validation process is failed
      else {
        form.executeResponseProcess(request, response, this.getServletContext());
      }
    }
    //! In the case of invalid user id
    catch (RuntimeException ex) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
    }
  }
}