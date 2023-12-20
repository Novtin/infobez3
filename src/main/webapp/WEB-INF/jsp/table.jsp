<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Human manager</title>
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" type="text/css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js" integrity="sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js" integrity="sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF" crossorigin="anonymous"></script>
    <link href="${pageContext.request.contextPath}/resources/css/style.css" type="text/css" rel="stylesheet">
</head>
<body style="text-align: center">
<c:choose>
    <c:when test="${not empty humanList}">
        <table class="table table-bordered" style="width: 40%">
            <tr>
                <td>№</td>
                <td>Имя</td>
                <td>Фамилия</td>
                <td>Действие</td>
            </tr>
            <c:forEach var="human" items="${humanList}">
                <tr>
                    <td>${human.id}</td>
                    <td>${human.name}</td>
                    <td>${human.surname}</td>
                    <td>
                        <form:form method="post" action="${pageContext.request.contextPath}/menu/delete/${human.id}">
                            <button type="submit" class="btn btn-light">Удалить</button>
                        </form:form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:when>
    <c:otherwise>
        <h2>Записей нет</h2>
    </c:otherwise>
</c:choose>
<footer>
    <a href="${pageContext.request.contextPath}/menu/" style="font-size: smaller">Вернуться на главную страницу</a>
</footer>
</body>
</html>