
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Make a Payment</title>
    </head>
    <body>
    <center>
        <h1>XYZ Drivers Association</h1>
        <h2>Make a Payment</h2>
        <p>
            <label><b>Date</b></label>
            <input type="text" placeholder="Enter Date" name="date" required>
        </p>
        <p>
            <label><b>Amount</b></label>
            <input type="text" placeholder="Enter Amount" name="amount" required>
        </p>

        <p>
            <button type="submit">Submit</button>

        </p>
        <p>
        <form action="membersDashboard.jsp">
            <input type="submit" value="Back to Dashboard" />
        </form>
    </p>
</center>
</body>
</html>
