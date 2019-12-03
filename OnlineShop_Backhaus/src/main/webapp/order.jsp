<%-- 
    Document   : order
    Created on : 20.11.2019, 10:59:43
    Author     : franz
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link href="css/style2.css" rel="stylesheet" type="text/css"/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Orders</h1>      
        <table>
            <tr><th>Order ID</th><th>Date</th><th>Details</th></tr>
            <c:forEach items="${orders}" var="o">
                <tr>
                    <td>
                        <c:out value="${o.orderid}"/>
                    </td>
                    <td>
                        <c:out value="${o.date}"/>
                    </td>
                    <td>
                        <input type="button" value="..." onclick="showArticles(${o.orderid})"/>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form>
            <input type="submit" value="Zurück" formaction="./shop.jsp" formmethod="post"/>
        </form>
        
        <br>
        <div id="details">
            
        </div>
        <script src="details.js" type="text/javascript"></script>
    </body>
</html>
