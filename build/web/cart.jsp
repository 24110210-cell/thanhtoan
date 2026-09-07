<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Murach's Java Servlets and JSP</title>
    <link rel="stylesheet" type="text/css" href="main.css">
</head>
<body>

    <h2>Your cart</h2>

    <c:choose>
        <c:when test="${empty cart.items}">
            <p>Your cart is currently empty.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Quantity</th>
                        <th>Description</th>
                        <th>Price</th>
                        <th>Amount</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${cart.items}">
                        <tr>
                            <td>
                                <form action="cart" method="post">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="productCode" value="${item.product.code}">
                                    <input type="text" name="quantity" value="${item.quantity}">
                                    <input type="submit" value="Update">
                                </form>
                            </td>
                            <td>${item.product.description}</td>
                            <td><fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="$"/></td>
                            <td><fmt:formatNumber value="${item.total}" type="currency" currencySymbol="$"/></td>
                            <td>
                                <form action="cart" method="post">
                                    <input type="hidden" name="action" value="remove">
                                    <input type="hidden" name="productCode" value="${item.product.code}">
                                    <input type="submit" value="Remove Item">
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <p class="note"><strong>To change the quantity</strong>, enter the new quantity and click on the Update button.</p>

    <div class="action-buttons">
        <form action="index.jsp" method="get">
            <input type="submit" value="Continue Shopping">
        </form>
        <form action="cart" method="post">
            <input type="hidden" name="action" value="checkout">
            <input type="submit" value="Checkout">
        </form>
    </div>

</body>
</html>