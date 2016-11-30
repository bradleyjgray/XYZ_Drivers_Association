<%-- 
    Document   : FinancialReports
    Created on : 29-Nov-2016, 21:27:37
    Author     : Luke James
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <style>
            p.padding {
                padding-left: 2cm;
            }
            p.padding2 {
                padding-left: 25%;
            }
        </style>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <center><title>Financial Reports</title></center>
</head>
<body>

<center><h1>Financial Reports</h1></center>
<br/><br/>
<p class="padding2">
    <%
        String noOfMembers = (String) request.getAttribute("memberCount");
        String claimTotal = (String) request.getAttribute("claimTotal");
        String recommendedCharge = (String) request.getAttribute("reccCharge");
        String incomeTotal = (String) request.getAttribute("totalIncome");

        out.print("<center>");
        out.print("Financial Summary<br/>");
        out.print("<table border=\"3\">");
        out.print("<tr>");
        out.print("<td>");
        out.print("Total Income (Previous 12 Months)</td><td> £" + incomeTotal + "</td>");
        out.print("</tr>");
        out.print("<tr>");
        out.print("<td>Number of Members (Count to Date):</td><td> " + noOfMembers + "</td>");
        out.print("</tr>");
        out.print("<tr>");
        out.print("<td> Claims Total (Last 12 Months):</td><td> £" + claimTotal + "</td>");
        out.print("</tr>");
        out.print("</table>");
        out.print("<br/>Charges Summary<br/>");
        out.print("<table border=\"3\">");
        out.print("<tr>");
        out.print("<td> Recommended Member Charge:</td><td> £" + recommendedCharge + "</td>");
        out.print("</tr>");
        out.print("</table>");
        out.print("</center>");

    %>
<center>

    <%        String chargeResult = (String) request.getAttribute("chargeResult");

        if (chargeResult != null) {
            out.println("<br/> <font color=red>Database Response ::" + chargeResult + "</font>");
        }

    %>
    <br/>
    <form action ="AdminServlet">
        <button name="request" type="submit" value="chargeMembers">Add Recommended Charge to Balance(s)</button>
    </form>

    <br/><br/>
    <form action="AdminDashboard.html">
          <button action="AdminDashboard.html" type="submit" value="listMembers"> Dashboard Menu </button>
    </form>
</center>
</p>
</body>
</html>
