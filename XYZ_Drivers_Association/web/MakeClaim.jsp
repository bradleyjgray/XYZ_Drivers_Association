<%-- 
    Document   : MakeClaim
    Created on : 29-Nov-2016, 23:08:56
    Author     : bgray
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Make a Claim</title>
    </head>
    <body>
        <center>
            <h1 style="font-size:300%;">Make a Claim</h1>
        </center>
    
    <h2>Account Details</h2>
        <div class="container">
            <form action="SubmitClaim">
                <p>
                <label><b>Description</b></label>
                <input type="text" placeholder="Description" name="description" required>
                </p>
                <p>
                <label><b>Amount(£)</b></label>
                <input type="text" placeholder="Amount" name="amount" required>
                </p>
                <p>
                <button type="submit">Submit</button>
                </p>
            </form>
        </div>
    </body>
</html>
