<%-- 
    Document   : pregledPodataka
    Created on : Jun 9, 2015, 3:28:18 PM
    Author     : Josip
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Podaci o korisniku</title>
    </head>
    <body>
        <h1>Podaci o korisniku </h1>
        Korisnik:<br>
        Korisnicko ime: ${sessionScope.korisnickoIme}<br>
        Raspoloživa sredstva: ${requestScope.sredstva}<br>

        <form action="${pageContext.servletContext.contextPath}/privatno/DodajSredstva" method="POST">
            Dodati nova sredstva <br/>
            Kolicina za dodati:<input type="number" name="dodatak"><br>
            <input type="submit" value="Dodaj">
        </form>

        <a href="${pageContext.servletContext.contextPath}/privatno/PregledTransakcija">Pregled transakcija</a><br>


    </body>
</html>
