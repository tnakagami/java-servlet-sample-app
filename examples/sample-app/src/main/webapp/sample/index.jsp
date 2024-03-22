<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/header.jsp" flush="true" />
<%@ page import="app.sample.models.User" %>
<%@ page import="java.util.Formatter" %>
<%@ page import="java.util.List" %>
<%
  request.setCharacterEncoding("UTF8");
  List<User> users = (List<User>)request.getAttribute("users");
  Formatter formatter = new Formatter(out);
%>
<div class="p-4">
  <div class="row mt-2">
    <div class="col">
      <p class="h2">Sample App</p>
    </div>
  </div>
  <div class="row p-2">
    <div class="col">
      <div class="row">
        <div class="col">
          <p class="h3 text-decoration-underline">Register/Update User</p>
        </div>
      </div>
      <div class="row row-cols-1 row-cols-md-3 g-2 mt-2">
        <div class="col">
          <select name="user-list" class="form-select" aria-label="User list">
            <%
              for (User user: users) {
                formatter.format("<option value=\"%d\">%s</option>", user.getID(), user.getName());
              }
            %>
          </select>
        </div>
        <div class="col">
          <%
            String urlPattern = "#";

            if (users.size() > 0) {
              User user = users.get(0);
              urlPattern = String.format("/sample-app/user/%d", user.getID());
            }
          %>
          <a href="<%= urlPattern %>" id="update-user" class="btn btn-primary w-100 custom-boxshadow-effect">
            Update user's information
          </a>
        </div>
        <div class="col">
          <a href="/sample-app/user/create-user" class="btn btn-success w-100 custom-boxshadow-effect">
            Register user
          </a>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
  (function () {
    const menus = {
      'update-user':   {name: 'user-list',   prefix: '/sample-app/user'},
    };
    for (const tagID of Object.keys(menus)) {
      const target = menus[tagID];
      const element = document.querySelector(`select[name="${target.name}"]`);
      element.addEventListener('change', (event) => {
        const value = event.target.value;
        const _tag = document.querySelector(`#${tagID}`);
        _tag.href = `${target.prefix}/${value}`;
      });
    }
  })();
</script>

<jsp:include page="/footer.jsp" />