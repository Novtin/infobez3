<%@ page import="java.math.BigInteger" %>
<%@ page import="javaClasses.DiffieHellman" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>


<%
    BigInteger P = (BigInteger) session.getAttribute("P");
    BigInteger G = (BigInteger) session.getAttribute("G");
    BigInteger openKey = (BigInteger) session.getAttribute("openKeyServer");
    BigInteger secretKey = DiffieHellman.getSecretKey();
    BigInteger openKeyClient = DiffieHellman.getOpenKey(P, G, secretKey);
    BigInteger secretCommonKey = DiffieHellman.getCommonSecretKey(openKey, secretKey, P);
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Human manager</title>
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" type="text/css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js" integrity="sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/3.1.9-1/crypto-js.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js" integrity="sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF" crossorigin="anonymous"></script>
    <link href="${pageContext.request.contextPath}/resources/css/style.css" type="text/css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/resources/js/encrypt.js"></script>
</head>
<script>
    console.log("P: ", <%= P %>);
    console.log("P точный: ", <%= session.getAttribute("P") %>);
    console.log("G: ", <%= G %>);
    console.log("openServerKey: ", <%= openKey.toString() %>);
    console.log("openClientKey: ", <%= openKeyClient.toString() %>);
    const G = <%= G %>;
    const openKeyClient = <%= openKeyClient.toString() %>;
    const secretCommonKey = <%= secretCommonKey.toString() %>;
    console.log("Вот общий секретный ключ: ", secretCommonKey);
    async function encryptHuman(event){
        event.preventDefault();
        let login = document.getElementById("login").value;
        let password = document.getElementById("password").value;
        document.getElementById("login").value = await encrypt(login, secretCommonKey);
        document.getElementById("password").value = await encrypt(password, secretCommonKey);
        document.getElementById("openKeyClient").value = openKeyClient;
        console.log(document.getElementById("login").value);
        console.log(document.getElementById("password").value);
        console.log(document.getElementById("openKeyClient").value);
        event.target.submit();
    }
</script>
<body>
<h2>Вход</h2>

<%
    String error = request.getParameter("error");
    String logout = request.getParameter("logout");
    if (error != null) {
%>
    <p style="color: red;">Неверный логин или пароль</p>
<%
} else if (logout != null) {
%>
    <p style="color: yellowgreen;">Вы успешно вышли из аккаунта</p>
<%
    }
%>

<c:set var="client" scope="request" value="${client}"/>
<form:form modelAttribute="client" method="post" onsubmit="encryptHuman(event);" action="/user/login" id="formSubmit">
    <table>
        <tr>
            <td>Логин:</td>
            <td><form:input path="login" id="login" required="true" minlength="4"/></td>
        </tr>
        <tr>
            <td>Пароль:</td>
            <td><form:input path="password" type="password" id="password" required="true" minlength="4"/></td>
        </tr>
        <tr>
            <td colspan="2" class="text-center">
                <br>
                <button type="submit" class="btn btn-light">Войти</button>
            </td>
        </tr>
    </table>
    <input type="hidden" id="openKeyClient" name="openKeyClient" />
</form:form>

<footer>
    <a href="${pageContext.request.contextPath}/user/registration" style="font-size: smaller">Зарегистрируйтесь, если нет аккаунта</a>
</footer>
</body>
</html>