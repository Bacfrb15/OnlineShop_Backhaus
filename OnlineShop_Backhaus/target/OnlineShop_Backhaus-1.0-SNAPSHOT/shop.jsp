<%-- 
    Document   : shop
    Created on : 20.11.2019, 10:59:34
    Author     : franz
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link href="css/style2.css" rel="stylesheet" type="text/css"/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Shop</h1>
        <div id="article">
            
        </div>
        <br>
        <form>
            <input type="submit" formmethod="post" formaction="./OnOrderServlet" value="Buy"/>
            <input type="submit" formmethod="post" formaction="./ShowOrderServlet" value="Show Orders"/>
        </form>
        <script src="index.js" type="text/javascript"></script>
        <script>showArticles()</script>
        <script src="shop.js" type="text/javascript"></script>
    </body>
</html>
