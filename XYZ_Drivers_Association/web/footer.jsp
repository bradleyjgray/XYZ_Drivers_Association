<%@page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>  

<%
    Date today = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    String date = dateFormat.format(today);
    SimpleDateFormat timeFormat = new SimpleDateFormat("h:m a");
    String time = timeFormat.format(today);    
    SimpleDateFormat zoneFormat = new SimpleDateFormat("z");
    String zone = zoneFormat.format(today);
%>

<p align="center"><font face="verdana"> Date: <%= date %> | Time: <%= time%> <%= zone %></font></p>