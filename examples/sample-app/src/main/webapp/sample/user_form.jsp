<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/header.jsp" flush="true" />
<%@ page import="app.sample.models.Role" %>
<%@ page import="java.util.Formatter" %>
<%@ page import="java.util.Objects" %>
<%@ page import="java.util.List" %>

<%
  request.setCharacterEncoding("UTF8");
  String error = (String)request.getAttribute("error");
  String action = (String)request.getAttribute("action");
  String name = (String)request.getAttribute("name");
  Role role = (Role)request.getAttribute("role");
%>

<div class="p-4">
  <div class="row mt-2">
    <div class="col">
      <p class="h2">Register/Update User</p>
    </div>
  </div>
  <div class="row p-2">
    <div class="col">
      <form action="<%= action %>" method="POST">
        <% if (Objects.nonNull(error)) { %>
        <div class="row mt-2">
          <div class="col">
            <strong class="error-message">Error: <%= error %></strong>
          </div>
        </div>
        <% } %>
        <div class="row row-cols-1 g-2 mt-2">
          <div class="col">
            <label for="username" class="form-label">Username</label>
            <input type="text" class="form-control" id="username" name="username" value="<%= name %>" placeholder="Enter the username" />
          </div>
          <div class="col">
            <label for="role" class="form-label">Role</label>
            <select name="role" id="role" class="form-select" aria-label="Role lists">
              <%
                List<Role> roles = (List<Role>)request.getAttribute("roles");
                Formatter formatter = new Formatter(out);
                int defaultId = role.getID();

                for (Role target : roles) {
                  int id = target.getID();
                  String attrName = (defaultId == id) ? "selected" : "";
                  formatter.format("<option value=\"%d\" %s>%s</option>", id, attrName, target.getLabel());
                }
              %>
            </select>
          </div>
        </div>
        <div class="row row-cols-1 row-cols-md-2 g-2 mt-2">
          <div class="col">
            <button type="submit" class="btn btn-primary w-100 custom-boxshadow-effect">
              Register/Update
            </button>
          </div>
          <div class="col">
            <a href="/sample-app" class="btn btn-secondary w-100 custom-boxshadow-effect">
              Back
            </a>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>

<jsp:include page="/footer.jsp" />