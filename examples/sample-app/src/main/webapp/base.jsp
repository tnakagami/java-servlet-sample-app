<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- core: Provides basic functions such as variable manipulation, conditional branching, control statements, and URL management. --%>
<%-- fmt:  Provides functions related to message format, locale handling, and numeric and date display formats. --%>
<%-- fn:   Provides functions for collections (List, Map, Array, etc.) and string manipulation. --%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
    <%-- Load Bootstrap CSS --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
    <style>
      .custom-boxshadow-effect {
        box-shadow: 0 3px 3px rgba(72, 78, 85, 0.6);
        transition: all 0.2s ease-out;
      }
      .custom-boxshadow-effect:hover {
        box-shadow: 0 10px 10px rgba(72, 78, 85, 0.6);
      }
      .error-message {
        color: #ea0000;
      }
    </style>
  </head>
  <body>
    <div class="container mx-auto p-2">
      ${param.content}
    </div>
  </body>
  <%-- Load Bootstrap javascript --%>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
          integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" crossorigin="anonymous"></script>
  <c:if test="${not empty param.script}">
    ${param.script}
  </c:if>
</html>
