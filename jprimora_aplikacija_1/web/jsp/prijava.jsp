<%-- 
    Document   : prijava
    Created on : Jun 9, 2015, 3:27:54 PM
    Author     : Josip
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Prijava</title>
    </head>
    <body>
        <h1>Prijava korisnika</h1>
        <form action="j_security_check" method="POST">
            Korisničko ime:<input type="text" name="j_username"><br>
            Lozinka:<input type="password" name="j_password">
            <input type="submit" value="Login">
        </form>

    </body>
</html>
