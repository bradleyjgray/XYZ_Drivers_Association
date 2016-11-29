<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>XYZ Member Management</title>
    </head>
    <body>
    <center>
        <h1>XYZ Drivers Association</h1>
        <h2> Member Management</h2>

        <div class="container">
            <form action="AdminServlet">
                <p>
                    <label><b>Username</b></label>
                    <input type="text" placeholder="Enter Username" name="username" required>
                </p>
                <p>
                    <button name="request" type="submit" value="processApplication">Upgrade</button> <button name="request" type="submit" value="suspendMember">Suspend</button>
                </p>
                <p>
            </form>       
        </div>
        <div class ="container">
                   <center>
                       <p> Toggle List: </p>
                       <form action="AdminServlet">
                       <button action="AdminServlet" name="request" type="submit" value="listMembers"> All Members </button>
                       <button action="AdminServlet" name="request" type="submit" value="listApplications"> Member Applications </button>
                       <button action="AdminServlet" name="request" type="submit" value="listBalances"> Outstanding Balances </button>
                       </form>
                   </center>
            
            
        </div>

        <%
            String genResult = (String) request.getAttribute("message");
            String genTable = (String) request.getAttribute("messageList");
            
            if (genResult != null){
            out.print("<p><font color=red>Database Message: " + genResult + "</font></p>");
            }
            out.print("Database List: <br/><br/>" + genTable);
        %>
    </center>
</body>
</html>
