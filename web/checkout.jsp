<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thanh toán đơn hàng</title>
    <link rel="stylesheet" type="text/css" href="main.css">
    <style>
        /* ===== CSS cho trang checkout ===== */
        .checkout-wrapper {
            max-width: 900px;
            margin: 20px auto;
            display: flex;
            gap: 30px;
            flex-wrap: wrap;
        }

        .checkout-box {
            flex: 1;
            min-width: 380px;
            background: #ffffff;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 25px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .checkout-box h3 {
            color: #008080;
            font-size: 18px;
            margin-top: 0;
            margin-bottom: 20px;
            border-bottom: 2px solid #008080;
            padding-bottom: 10px;
        }

        /* Bảng đơn hàng */
        .order-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 15px;
        }

        .order-table th,
        .order-table td {
            padding: 10px;
            border-bottom: 1px solid #eee;
            text-align: left;
            font-size: 14px;
        }

        .order-table th {
            background: #f8f9fa;
            font-weight: bold;
            color: #555;
        }

        .order-table td.amount,
        .order-table th.amount {
            text-align: right;
        }

        .order-total {
            display: flex;
            justify-content: space-between;
            padding: 12px 10px;
            background: #e8f5f5;
            border-radius: 4px;
            font-size: 16px;
            font-weight: bold;
            color: #008080;
        }

        /* Form nhập thông tin */
        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            font-weight: bold;
            color: #333;
            margin-bottom: 6px;
            font-size: 14px;
        }

        .form-group input {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
            box-sizing: border-box;
            transition: border-color 0.2s;
        }

        .form-group input:focus {
            border-color: #008080;
            outline: none;
            box-shadow: 0 0 4px rgba(0,128,128,0.2);
        }

        .form-group input::placeholder {
            color: #aaa;
        }

        /* Nút thanh toán VNPay */
        .btn-vnpay {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #005ba1 0%, #0077c8 100%);
            color: #ffffff;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            margin-top: 10px;
            transition: all 0.3s;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
        }

        .btn-vnpay:hover {
            background: linear-gradient(135deg, #004080 0%, #0060a0 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,91,161,0.3);
        }

        .btn-vnpay:active {
            transform: translateY(0);
        }

        .vnpay-logo {
            font-weight: bold;
            color: #fff;
            background: #e30613;
            padding: 2px 6px;
            border-radius: 3px;
            font-size: 12px;
            letter-spacing: 0.5px;
        }

        /* Thông báo giỏ hàng trống */
        .empty-cart {
            text-align: center;
            padding: 40px;
            color: #888;
        }

        .empty-cart a {
            color: #008080;
            text-decoration: none;
            font-weight: bold;
        }
    </style>
</head>
<body>

    <h2 style="text-align:center; margin-bottom:25px;">Thông tin thanh toán</h2>

    <c:if test="${empty cart.items}">
        <div class="empty-cart">
            <p style="font-size:18px;">🛒 Giỏ hàng trống</p>
            <p><a href="index.jsp">← Quay về trang chủ</a></p>
        </div>
    </c:if>

    <c:if test="${not empty cart.items}">
        <div class="checkout-wrapper">

            <%-- ===== CỘT TRÁI: ĐƠN HÀNG ===== --%>
            <div class="checkout-box">
                <h3>📦 Đơn hàng của bạn</h3>

                <table class="order-table">
                    <thead>
                        <tr>
                            <th>Sản phẩm</th>
                            <th class="amount">SL</th>
                            <th class="amount">Đơn giá</th>
                            <th class="amount">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cart.items}">
                            <tr>
                                <td>${item.product.description}</td>
                                <td class="amount">${item.quantity}</td>
                                <td class="amount"><fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="$"/></td>
                                <td class="amount"><fmt:formatNumber value="${item.total}" type="currency" currencySymbol="$"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <div class="order-total">
                    <span>Tổng cộng:</span>
                    <span><fmt:formatNumber value="${cart.total}" type="currency" currencySymbol="$"/></span>
                </div>
            </div>

            <%-- ===== CỘT PHẢI: FORM NHẬP ===== --%>
            <div class="checkout-box">
                <h3>👤 Thông tin người nhận</h3>

                <form action="checkout" method="post">
                    <div class="form-group">
                        <label for="customerName">Họ tên *</label>
                        <input type="text" id="customerName" name="customerName"
                               placeholder="Nguyễn Văn A" required>
                    </div>

                    <div class="form-group">
                        <label for="customerEmail">Email *</label>
                        <input type="email" id="customerEmail" name="customerEmail"
                               placeholder="email@example.com" required>
                    </div>

                    <div class="form-group">
                        <label for="customerPhone">Số điện thoại *</label>
                        <input type="text" id="customerPhone" name="customerPhone"
                               placeholder="0912 345 678" required>
                    </div>

                    <div class="form-group">
                        <label for="customerAddress">Địa chỉ *</label>
                        <input type="text" id="customerAddress" name="customerAddress"
                               placeholder="Số nhà, đường, quận, thành phố" required>
                    </div>

                    <button type="submit" class="btn-vnpay">
                        <span class="vnpay-logo">VNPAY</span>
                        <span>Thanh toán qua VNPay</span>
                    </button>
                </form>
            </div>

        </div>
    </c:if>

</body>
</html>