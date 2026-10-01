<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Murach's Java Servlets and JSP</title>
    <link rel="stylesheet" type="text/css" href="main.css">
</head>
<body>

    <%-- ===== THÔNG BÁO KẾT QUẢ THANH TOÁN ===== --%>
    <c:if test="${param.msg == 'paid'}">
        <div style="padding:15px; margin-bottom:20px; background:#e8f5e9;
                    border-left:5px solid #27ae60; color:#1b5e20; border-radius:4px;">
            <strong>✅ Thanh toán thành công!</strong><br>
            Mã đơn hàng: <strong>${param.orderId}</strong><br>
            Cảm ơn bạn đã đặt hàng. Đơn hàng sẽ được xử lý trong thời gian sớm nhất.
        </div>
    </c:if>

    <c:if test="${param.msg == 'failed'}">
        <div style="padding:15px; margin-bottom:20px; background:#ffebee;
                    border-left:5px solid #c0392b; color:#7f0000; border-radius:4px;">
            <strong>❌ Thanh toán không thành công.</strong><br>
            Vui lòng thử lại hoặc liên hệ bộ phận hỗ trợ.
        </div>
    </c:if>

    <h2>CD list</h2>

    <table>
        <thead>
            <tr>
                <th>Description</th>
                <th>Price</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>86 (the band) - True Life Songs and Pictures</td>
                <td>$14.95</td>
                <td>
                    <form action="cart" method="post">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="productCode" value="8601">
                        <input type="submit" value="Add To Cart">
                    </form>
                </td>
            </tr>
            <tr>
                <td>Paddlefoot - The first CD</td>
                <td>$12.95</td>
                <td>
                    <form action="cart" method="post">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="productCode" value="pf01">
                        <input type="submit" value="Add To Cart">
                    </form>
                </td>
            </tr>
            <tr>
                <td>Paddlefoot - The second CD</td>
                <td>$14.95</td>
                <td>
                    <form action="cart" method="post">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="productCode" value="pf02">
                        <input type="submit" value="Add To Cart">
                    </form>
                </td>
            </tr>
            <tr>
                <td>Joe Rut - Genuine Wood Grained Finish</td>
                <td>$14.95</td>
                <td>
                    <form action="cart" method="post">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="productCode" value="jr01">
                        <input type="submit" value="Add To Cart">
                    </form>
                </td>
            </tr>
        </tbody>
    </table>

</body>
</html>