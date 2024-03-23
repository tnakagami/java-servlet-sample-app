<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:import url="/base.jsp" charEncoding="UTF-8">
  <c:param name="content">
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
                <c:forEach var="user" items="${users}">
                  <option value='<c:out value="${user.getID()}" />'>
                    <c:out value="${user.getName()}" />
                  </option>
                </c:forEach>
              </select>
            </div>
            <div class="col">
              <%-- Set URL of the user form to "updateUserUrl" variable --%>
              <c:choose>
                <c:when test="${users.size() > 0}">
                  <c:set var="updateUserUrl" scope="page">/sample-app/user/${users.get(0).getID()}</c:set>
                </c:when>
                <c:otherwise>
                  <c:set var="updateUserUrl" scope="page">#</c:set>
                </c:otherwise>
              </c:choose>
              <a href="${updateUserUrl}" id="update-user" class="btn btn-primary w-100 custom-boxshadow-effect">
                Update user's information
              </a>
              <%-- Remove "updateUserUrl" variable --%>
              <c:remove var="updateUserUrl" scope="page" />
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
  </c:param>
  <c:param name="script">
    <script>
      (function () {
        const menus = {
          'update-user':   {name: 'user-list',   prefix: '/sample-app/user'},
        };
        for (const tagID of Object.keys(menus)) {
          const target = menus[tagID];
          const element = document.querySelector('select[name="' + target.name + '"]');
          element.addEventListener('change', (event) => {
            const value = event.target.value;
            const _tag = document.querySelector('#' + tagID);
            _tag.href = target.prefix + '/' + value;
          });
        }
      })();
    </script>
  </c:param>
</c:import>
