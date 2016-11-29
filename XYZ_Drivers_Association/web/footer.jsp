<%@page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>  

<%
    Date today = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    String date = dateFormat.format(today);
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    String time = timeFormat.format(today);    
    SimpleDateFormat zoneFormat = new SimpleDateFormat("z");
    String zone = zoneFormat.format(today);
    com.web.WeatherSOAP weather = new com.web.WeatherSOAP();
    com.web.WeatherSOAP.weather();
    com.web.WeatherSOAP.XMLread();
    out.print(com.web.WeatherSOAP.city);
%>

<p align="center"><font face="verdana"> Date: <%= date %> | Time: <%= time%> <%= zone %></font></p>
