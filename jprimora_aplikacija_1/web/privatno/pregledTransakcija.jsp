<%-- 
    Document   : pregledTransakcija
    Created on : Jun 9, 2015, 4:36:11 PM
    Author     : Josip
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Pregled transakcija</title>
        <link href="${pageContext.servletContext.contextPath}/css/displaytag.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <h1>Predgled transakcija</h1>
        
        <a href="${pageContext.servletContext.contextPath}/privatno/PregledPodataka">Pregled podataka</a><br>
        
        <h2>Filteri</h2>
        <form action="${pageContext.servletContext.contextPath}/privatno/PregledTransakcijaFilter" method="POST">
            Vrijeme od:<input type="datetime" name="vrijemeOd"><br>
            Vrijeme do:<input type="datetime" name="vrijemeDo"><br>
            IP adresa:<input type="text" name="ipAdresa"><br>
            <input type="submit" value="Filtriraj">
        </form>
        
        <display:table name="${sessionScope.tablica}" pagesize="${sessionScope.stranicenje}">
            <display:column property="ipAdresa" sortable="true"/>
            <display:column property="korisnik" sortable="true"/>
            <display:column property="zahtjev" sortable="true"/>
            <display:column property="odgovor" sortable="true"/>
            <display:column property="trajanje" sortable="true"/>
            <display:column property="vrijeme" sortable="true"/>
            <display:column property="status" sortable="true"/>
            <display:column property="URL" sortable="true"/>
            <display:column property="sredstva" sortable="true"/>
            
        </display:table>
    </body>
</html>
