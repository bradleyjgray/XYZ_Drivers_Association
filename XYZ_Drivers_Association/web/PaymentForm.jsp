<%-- 
    Document   : PaymentForm
    Created on : 30-Nov-2016, 15:11:23
    Author     : bgray
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Payment</title>
    </head>
    <body>
        <center>
            <h1 style="font-size:300%;">Make a Payment</h1> 
            <form action="ProcessPayment">
                <p>
                <label><b>Amount(£)</b></label>
                <input type="text" placeholder="Amount" name="amount" required>
                </p>
                <p>
                <select name="paymentType">
                    <option value="memberFee">Membership Fee</option>
                    <option value="balance">Balance Outstanding</option>
                </select>
                <p>
                <button type="submit">Submit</button>
                </p>
            </form>
        </center>
    </body>
</html>
