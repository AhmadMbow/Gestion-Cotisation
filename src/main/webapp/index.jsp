<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Page d'entrée — redirige vers le formulaire de connexion
    response.sendRedirect(request.getContextPath() + "/login");
%>
