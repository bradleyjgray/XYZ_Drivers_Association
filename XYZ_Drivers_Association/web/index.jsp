<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Home Page</title>
    </head>
    <body>
        <jsp:include page="header.jsp"/>
    <center>
        <form action="login.jsp">
            <input type="submit" value="Login" />
        </form>
</center>
        <jsp:include page="footer.jsp"/>
</body>
</html>
