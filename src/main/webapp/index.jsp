<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Page d'entrée — l'application Cotisen (version web, servie sur /app/).
    // L'interface d'administration d'origine reste accessible sur /login.
    response.sendRedirect(request.getContextPath() + "/app/");
%>
