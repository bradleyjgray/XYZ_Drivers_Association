
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>XYZ Member's Dashboard</title>
    </head>
    <body>
    <centre>
        <h1>XYZ Drivers Association</h1>
        <h2>Member's Dashboard</h2>
    </centre>

    <div class="container">
        <p>
        <form action="checkStatus.jsp">
            <input type="submit" value="Check Claim Status" />
        </form>
        </p>
        
        <p>
        <form action="makeClaim.jsp">
            <input type="submit" value="Make a Claim" />
        </form>
        </p>
        
        <p>
        <form action="makePayment.jsp">
            <input type="submit" value="Make a Payment" />
        </form>
        </p>
    </div> 

</body>
</html>
