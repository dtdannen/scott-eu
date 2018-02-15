<!DOCTYPE html>
<%--
 Copyright (c) 2011, 2012, 2017 IBM Corporation and others.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 and Eclipse Distribution License v. 1.0 which accompanies this distribution.

 The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 and the Eclipse Distribution License is available at
 http://www.eclipse.org/org/documents/edl-v10.php.

 Contributors:

  Sam Padgett     - initial API and implementation
  Michael Fiedler - adapted for OSLC4J
  Jad El-khoury   - initial implementation of code generator (422448)
  Frédéric Loiret - Switch the template to Bootstrap (519699)
  Frédéric Loiret - Support for UI Preview (494303)
  Andrii Berezovskyi - Support for UI Preview (494303)

 This file is generated by org.eclipse.lyo.oslc4j.codegenerator
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@page import="org.eclipse.lyo.oslc4j.core.model.ServiceProvider"%>
<%@page import="java.util.List" %>
<%@page import="se.ericsson.cf.scott.sandbox.resources.LessOrEqual"%>
<%--
Start of user code imports
--%>
<%--
End of user code
--%>

<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>

<%
  LessOrEqual aLessOrEqual = (LessOrEqual) request.getAttribute("aLessOrEqual");
%>
<%--
Start of user code getRequestAttributes
--%>
<%--
End of user code
--%>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <title><%= aLessOrEqual.toString(true) %></title>

  <link href="<c:url value="/static/css/bootstrap-4.0.0-beta.min.css"/>" rel="stylesheet">
  <link href="<c:url value="/static/css/adaptor.css"/>" rel="stylesheet">

  <script src="<c:url value="/static/js/jquery-3.2.1.min.js"/>"></script>
  <script src="<c:url value="/static/js/popper-1.11.0.min.js"/>"></script>
  <script src="<c:url value="/static/js/bootstrap-4.0.0-beta.min.js"/>"></script>
  <script src="<c:url value="/static/js/preview.js"/>"></script>

  <%--
Start of user code (RECOMMENDED) headStuff
  --%>
  <%--
End of user code
  --%>
</head>


<body>

<nav class="navbar sticky-top navbar-light bg-light">
  <div class="container">
  <a class="navbar-brand" href="<c:url value="/services/catalog/singleton"/>">WarehouseController</a>
  </div>
</nav>

<!-- Begin page content -->
<div class="container">
    <div class="page-header">
        <h1>LessOrEqual resource</h1>
        <p class="lead">URI:&nbsp;
      <a href="<%= aLessOrEqual.getAbout() %>"><%= aLessOrEqual.getAbout() %></a>
        </p>
    </div>
        <%--
Start of user code (RECOMMENDED) bodyStuff1
        --%>
        <%--
End of user code
        --%>

        <h2>Properties</h2>

        <div>
          <dl class="row">
            <dt  class="col-sm-2 text-right">rightPredicate</dt>
            <dd class="col-sm-9"><%= aLessOrEqual.rightPredicateToHtml()%></dd>
          </dl>
          <dl class="row">
            <dt  class="col-sm-2 text-right">leftPredicate</dt>
            <dd class="col-sm-9"><%= aLessOrEqual.leftPredicateToHtml()%></dd>
          </dl>
        </div>
      </div>
    </div>

  <footer class="footer">
    <div class="container">
      <p class="text-muted">
        OSLC Adaptor was generated using <a href="http://eclipse.org/lyo">Eclipse Lyo</a> 2.3.0-SNAPSHOT.
      </p>
    </div>
  </footer>
</body>
</html>
