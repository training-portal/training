<%@ taglib prefix="duration" uri="/WEB-INF/custom_tags/formatDuration" %>
<%@ taglib prefix="localDateTime" uri="/WEB-INF/custom_tags/formatLocalDateTime" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="title.time.up"/></title>
    <c:import url="../fragment/head.jsp"/>
</head>
<body>
<c:import url="../fragment/navbar.jsp"/>
<div class="container">
    <h2><spring:message code="oops"/>... <spring:message code="quiz.congratulations.time"/></h2>
    <div class="row no-gutters align-items-center highlight-danger">
        <div class="col-auto mr-3">
            <img src="${pageContext.request.contextPath}/resources/icons/icon-danger.png"
                 width="25" height="25">
        </div>
        <div class="col">
            <spring:message code="quiz.congratulations.forcibly"/>&nbsp;
            <strong><c:out value="${quiz.quizName}"/></strong>&nbsp;
            <spring:message code="quiz.congratulations.with"/>&nbsp;
            ${quiz.attempt}&nbsp;<spring:message code="quiz.congratulations.attempt"/>
        </div>
    </div>
    <table class="col-lg-6 table-info">
        <tr>
            <td><spring:message code="quiz.result"/></td>
            <td>${quiz.result}/${quiz.score}</td>
        </tr>
        <tr>
            <td><spring:message code="quiz.time.spent"/></td>
            <td><duration:format value="${quiz.timeSpent}"/>&nbsp;/&nbsp;
                <duration:format value="${quiz.passingTime}"/></td>
        </tr>
        <tr>
            <td><spring:message code="quiz.passed"/></td>
            <td><localDateTime:format value="${quiz.finishDate}"/></td>
        </tr>
    </table>
    <c:if test="${quiz.explanation ne null}">
        <div class="col-lg-6">
            <strong><spring:message code="quiz.explanation"/>: </strong><c:out value="${quiz.explanation}"/>
        </div>
    </c:if>
    <sec:authorize access="hasRole('ROLE_STUDENT')">
        <div><a href="/student/quizzes/${quiz.quizId}" class="btn btn-primary btn-wide">
            <spring:message code="quiz.back"/>
        </a></div>
    </sec:authorize>
    <sec:authorize access="hasRole('ROLE_TEACHER')">
        <div><a href="/teacher/quizzes/${quiz.quizId}" class="btn btn-primary btn-wide">
            <spring:message code="quiz.back"/>
        </a></div>
    </sec:authorize>
</div>
<br>
</body>
</html>
