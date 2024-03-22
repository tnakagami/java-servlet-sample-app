package app.sample;

import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.lang.RuntimeException;
import java.util.Objects;
import java.util.List;
//! local package
import app.sample.utils.ParseUrl;
import app.sample.models.User;
import app.sample.models.Role;

@WebServlet(name="UserForm", urlPatterns={"/user/create-user", "/user/*"})
public class UserForm extends HttpServlet {
  private final String successfulURL = "/sample-app";

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html;charset=UTF-8");

    //! Parse URL pattern
    ParseUrl urlState = new ParseUrl(request.getPathInfo());
    //! In the case of being set user id
    if (!urlState.isInvalid()) {
      String name = "";
      Role role = Role.Viewer;

      if (!urlState.isCreation()) {
        List<User> users = User.getUsers(String.format("WHERE id = %d", urlState.getID()));

        //! In the case of valid user id
        if (users.size() > 0) {
          User user = users.get(0);
          name = user.getName();
          role = user.getRole();
        }
        //! In the case of invalid user id
        else {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bab Request");
          return;
        }
      }
      //! Get Valid Roles
      List<Role> roles = Role.getValidRoles();
      //! Setup as attributes
      request.setAttribute("error", null);
      request.setAttribute("action", request.getRequestURI());
      request.setAttribute("name", name);
      request.setAttribute("role", role);
      request.setAttribute("roles", roles);
      //! Page transition process
      ServletContext context = this.getServletContext();
      RequestDispatcher dispather = context.getRequestDispatcher("/sample/user_form.jsp");
      dispather.forward(request, response);
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

    //! Parse URL pattern
    ParseUrl urlState = new ParseUrl(request.getPathInfo());
    //! Get parameters
    String name = request.getParameter("username");
    String _role = request.getParameter("role");
    //! Validation of parameters
    if ((!urlState.isInvalid()) && (Objects.nonNull(_role))) {
      int role = Integer.parseInt(_role);
      int id = urlState.getID();

      try {
        //! In the case of creating user
        if (urlState.isCreation()) {
          User.createUser(name, role);
        }
        //! In the case of updating user information
        else {
          User.updateUser(id, name, role);
        }
        //! Page transition process
        response.sendRedirect(successfulURL);
      }
      //! In the case of validation error
      catch (RuntimeException ex) {
        if (Objects.isNull(name)) {
          name = "";
        }
        //! Get roles
        List<Role> roles = Role.getValidRoles();
        //! Setup as attributes
        request.setAttribute("error", ex.getMessage());
        request.setAttribute("action", request.getRequestURI());
        request.setAttribute("name", name);
        request.setAttribute("role", Role.getRole(role));
        request.setAttribute("roles", roles);
        //! Page transition process
        ServletContext context = this.getServletContext();
        RequestDispatcher dispather = context.getRequestDispatcher("/sample/user_form.jsp");
        dispather.forward(request, response);
      }
      //! In the case of SQL exception
      catch (SQLException ex) {
        List<User> users = User.getUsers(String.format("WHERE id = %d", id));

        //! In the case of valid user id
        if (users.size() > 0) {
          String err = "Occur an error inside the HTTP server which execute the SQL statement.";
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, err);
        }
        //! In the case of invalid user id
        else {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bab Request");
        }
      }
    }
  }
}