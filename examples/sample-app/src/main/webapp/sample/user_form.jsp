<%@ include file="/header.jsp" %>
<div class="p-4">
  <div class="row mt-2">
    <div class="col">
      <p class="h2">Register/Update User</p>
    </div>
  </div>
  <div class="row p-2">
    <div class="col">
      <form action="${action}" method="POST">
        <c:if test="${not empty error}">
          <div class="row mt-2">
            <div class="col">
              <strong class="error-message">Error: ${error}</strong>
            </div>
          </div>
        </c:if>
        <div class="row row-cols-1 g-2 mt-2">
          <div class="col">
            <label for="username" class="form-label">Username</label>
            <input type="text" class="form-control" id="username" name="username" value="${name}" placeholder="Enter the username" />
          </div>
          <div class="col">
            <label for="role" class="form-label">Role</label>
            <select name="role" id="role" class="form-select" aria-label="Role lists">
              <c:set var="defaultID" scope="page">${role.getID()}</c:set>
              <c:forEach var="target" items="${roles}">
                <c:set var="targetID" scope="page">${target.getID()}</c:set>
                <option value='<c:out value="${targetID}" />' <c:if test="${defaultID == targetID}">selected</c:if>>
                  ${target.getLabel()}
                </option>
                <c:remove var="targetID" scope="page" />
              </c:forEach>
              <c:remove var="defaultID" scope="page" />
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

<%@ include file="/footer.jsp" %>