package app.sample;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.RuntimeException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.sql.SQLException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//! local package
import app.sample.utils.UrlChecker;
import app.sample.models.User;
import app.sample.models.Role;

@WebServlet(name="UserForm", urlPatterns={"/user/create-user", "/user/*"})
public class UserForm extends HttpServlet {
  //! variable-setter-pairs
  private Map<String, String> pairs = new LinkedHashMap<String, String>() {
    {
      put("username", "setName");
      put("userrole", "setRole");
    }
  };

  private User getInstance(HttpServletRequest request, UrlChecker checker) {
    String methodName = request.getMethod();
    User user = User.getDefaultUser(checker.getID(), checker.isCreation());

    if ("POST".equals(methodName.toUpperCase())) {
      Class<?> target = user.getClass();

      for (var key: pairs.keySet()) {
        //! Get body data
        String value = request.getParameter(key);

        try {
          //! Call the User's instance method
          Method method = target.getMethod(pairs.get(key), new Class[]{String.class});
          method.invoke(user, value);
        }
        catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException ex) {
          ex.printStackTrace();
        }
      }
    }

    return user;
  }

  private void executeResponseProcess(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
      //! Setup as attributes
      request.setAttribute("errors", user.getErrors());
      request.setAttribute("action", request.getRequestURI());
      request.setAttribute("user", user);
      request.setAttribute("roles", Role.getValidRoles());
      //! Page transition process
      ServletContext context = getServletContext();
      RequestDispatcher dispather = context.getRequestDispatcher("/sample/user_form.jsp");
      dispather.forward(request, response);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html;charset=UTF-8");

    //! Check URL pattern
    UrlChecker checker = new UrlChecker(request.getPathInfo());
    //! In the case of being set user id
    if (checker.isValid()) {
      User user = getInstance(request, checker);

      try {
        //! Request's validation
        user.checkError();
        executeResponseProcess(request, response, user);
      }
      //! In the case of invalid user id
      catch (RuntimeException ex) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bab Request");
      }
    }
    //! In the case of not being set user id
    else {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested resource is not available.");
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html;charset=UTF-8");

    //! Check URL pattern
    UrlChecker checker = new UrlChecker(request.getPathInfo());
    //! Validation of parameters
    if (checker.isValid()) {
      User user = getInstance(request, checker);

      try {
        //! Form validation
        user.checkError();
        //! Record model data to Database
        user.save();
        //! Page transition process
        response.sendRedirect(request.getContextPath());
      }
      //! In the case of validation error
      catch (RuntimeException ex) {
        executeResponseProcess(request, response, user);
      }
      //! In the case of SQL exception
      catch (SQLException ex) {
        String err = "Occur an error inside the HTTP server which execute the SQL statement.";
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, err);
      }
    }
  }
}