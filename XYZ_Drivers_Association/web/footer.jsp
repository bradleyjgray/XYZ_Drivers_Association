<%@page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.io.BufferedReader" %>
<%@page import="java.io.IOException" %>
<%@page import="java.io.InputStreamReader" %>
<%@page import="java.net.HttpURLConnection" %>
<%@page import="java.net.MalformedURLException" %>
<%@page import="java.net.URL" %>


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
    String footerc = (com.web.WeatherSOAP.cloud);
    int celsius = (com.web.WeatherSOAP.celsius);
%>

<p align="center"><font face="verdana"> Date: <%= date %> | Time: <%= time %> <%= zone %></font></p>
<p align="center"><font face="verdana"> Current Weather: <%= celsius %>°C with <%= footerc %></font></p>
