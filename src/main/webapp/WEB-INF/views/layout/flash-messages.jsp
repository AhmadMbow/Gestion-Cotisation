<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Lit et efface les flash messages (placés en session par FlashUtil) --%>
<c:set var="flashSuccess" value="${sessionScope.flashSuccess}"/>
<c:set var="flashError" value="${sessionScope.flashError}"/>
<c:remove var="flashSuccess" scope="session"/>
<c:remove var="flashError" scope="session"/>

<c:if test="${not empty flashSuccess}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <i class="fas fa-check-circle mr-1"></i> ${flashSuccess}
    </div>
</c:if>
<c:if test="${not empty flashError}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <i class="fas fa-exclamation-circle mr-1"></i> ${flashError}
    </div>
</c:if>
