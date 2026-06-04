<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Lit et efface les flash messages (placés en session par FlashUtil) --%>
<c:set var="flashSuccess" value="${sessionScope.flashSuccess}"/>
<c:set var="flashError" value="${sessionScope.flashError}"/>
<c:remove var="flashSuccess" scope="session"/>
<c:remove var="flashError" scope="session"/>

<c:if test="${not empty flashSuccess}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle me-1"></i> ${flashSuccess}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
<c:if test="${not empty flashError}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-1"></i> ${flashError}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
