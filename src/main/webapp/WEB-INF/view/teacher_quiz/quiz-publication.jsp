<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="title.publication"/></title>
    <c:import url="../fragment/head.jsp"/>
    <script>
        $(document).ready(function () {
            $("[id*='group']").change(function () {
                var studentsCheckboxes = $(this).parents(".card").find("[id*='student']");
                if ($(this).prop("checked")) {
                    studentsCheckboxes.prop("checked", true);
                } else {
                    studentsCheckboxes.prop("checked", false);
                }
            });

            $("[id*='collapse'] [id*='student']").change(function () {
                var allChecked = true;
                var section = $(this).parents("[id*='collapse']");
                section.find("[id*='student']").each(function () {
                    if ($(this).prop("checked") === false) {
                        allChecked = false;
                    }
                });

                var groupCheckbox = $(this).parents(".card").find("[id*='group']");
                if (allChecked) {
                    groupCheckbox.prop("checked", true);
                } else {
                    groupCheckbox.prop("checked", false);
                }
            });

            $("#publicationForm").submit(function () {
                var checkboxes = $("[id*='group']:checked, [id*='student']:checked");
                if (checkboxes.length === 0) {
                    alert('<spring:message code="group.select.student"/>');
                    return false;
                }
                $("[id*='group']:checked").prop("disabled", true);
                return true;
            });
        });
    </script>
</head>
<body>
<c:import url="../fragment/navbar.jsp"/>
<div class="container">
    <h2><c:out value="${quiz.name}"/></h2>
    <c:choose>
        <c:when test="${empty groups && empty studentsWithoutGroup}">
            <div class="row no-gutters align-items-center highlight-primary">
                <div class="col-auto mr-3">
                    <img src="${pageContext.request.contextPath}/resources/icons/icon-primary.png"
                         width="25" height="25">
                </div>
                <div class="col">
                    <spring:message code="quiz.publish.all.published"/>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <form id="publicationForm" action="/teacher/quizzes/${quiz.quizId}/publication" method="post">
                <div class="row">
                    <div class="col-sm-4">
                        <h3><spring:message code="quiz.publish.publication"/></h3>
                    </div>
                    <div class="col-sm-2 align-self-end">
                        <button type="submit" class="btn btn-success btn-wide" style="margin-bottom: 20px">
                            <i class="fa fa-share-square-o"></i> <spring:message code="quiz.publish.publish"/>
                        </button>
                    </div>
                </div>
                <c:if test="${quiz.teacherQuizStatus eq 'UNPUBLISHED'}">
                    <div class="row no-gutters align-items-center highlight-danger">
                        <div class="col-auto mr-3">
                            <img src="${pageContext.request.contextPath}/resources/icons/icon-danger.png"
                                 width="25" height="25">
                        </div>
                        <div class="col">
                            <spring:message code="quiz.publish.no.edit"/>
                        </div>
                    </div>
                </c:if>
                <div class="row no-gutters align-items-center highlight-primary">
                    <div class="col-auto mr-3">
                        <img src="${pageContext.request.contextPath}/resources/icons/icon-primary.png"
                             width="25" height="25">
                    </div>
                    <div class="col">
                        <spring:message code="quiz.publish.select.students"/>
                    </div>
                </div>
                <div class="row">
                    <c:if test="${not empty groups}">
                        <div class="col-sm-6">
                            <div class="accordion-header"><spring:message code="group.groups"/></div>
                            <c:forEach items="${groups}" var="group">
                                <div class="card">
                                    <div class="card-header" id="heading${group.groupId}">
                                        <button type="button" class="btn-link" data-toggle="collapse"
                                                data-target="#collapse${group.groupId}"
                                                aria-expanded="false" aria-controls="collapse${group.groupId}">
                                            <c:out value="${group.name}"/>
                                        </button>
                                        <div class="custom-control custom-checkbox right">
                                            <input type="checkbox" id="group${group.groupId}"
                                                   name="group${group.groupId}"
                                                   value="${group.groupId}" class="custom-control-input">
                                            <label for="group${group.groupId}" class="custom-control-label"></label>
                                        </div>
                                    </div>
                                    <div id="collapse${group.groupId}" class="collapse"
                                         aria-labelledby="heading${group.groupId}">
                                        <div class="card-body">
                                            <c:forEach items="${students[group.groupId]}" var="student">
                                                <div class="row">
                                                    <div class="col-9 offset-1">
                                                            ${student.lastName} ${student.firstName}
                                                    </div>
                                                    <div class="col-2">
                                                        <div class="custom-control custom-checkbox">
                                                            <input type="checkbox" id="student${student.userId}"
                                                                   name="student${student.userId}"
                                                                   value="${student.userId}"
                                                                   class="custom-control-input">
                                                            <label for="student${student.userId}"
                                                                   class="custom-control-label"></label>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:if>
                    <c:if test="${not empty studentsWithoutGroup}">
                        <div class="col-sm-6">
                            <table class="table">
                                <tr>
                                    <th style="width: 90%"><spring:message code="quiz.students.without.group"/></th>
                                    <th style="width: 10%"></th>
                                </tr>
                                <c:forEach items="${studentsWithoutGroup}" var="student">
                                    <tr>
                                        <td>${student.lastName} ${student.firstName}</td>
                                        <td>
                                            <div class="custom-control custom-checkbox">
                                                <input type="checkbox" id="student${student.userId}"
                                                       name="student${student.userId}"
                                                       value="${student.userId}" class="custom-control-input">
                                                <label for="student${student.userId}"
                                                       class="custom-control-label"></label>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </c:if>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
    <button class="btn btn-primary" onclick="window.history.go(-1);"><spring:message code="back"/></button>
</div>
<br>
</body>
</html>
