package murach.controllers;

import murach.business.Cart;
import murach.business.Order;
import murach.data.OrderDB;
import murach.util.VNPayUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private static final long USD_TO_VND = 24000L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.getItems().isEmpty()) {
            response.sendRedirect("cart.jsp");
            return;
        }

        String customerName    = request.getParameter("customerName");
        String customerEmail   = request.getParameter("customerEmail");
        String customerPhone   = request.getParameter("customerPhone");
        String customerAddress = request.getParameter("customerAddress");

        String orderId = "DH" + System.currentTimeMillis();
        Order order = new Order(orderId, customerName, customerEmail,
                                customerPhone, customerAddress,
                                cart.getItems(), cart.getTotal());

        OrderDB.insert(order);
        session.setAttribute("pendingOrderId", orderId);

        long amountVND = Math.round(cart.getTotal() * USD_TO_VND);

        String ipAddress = request.getRemoteAddr();
        if (ipAddress == null || ipAddress.isEmpty()) ipAddress = "127.0.0.1";

        String orderInfo = "Thanh toan don hang " + orderId;
        String paymentUrl = VNPayUtil.createPaymentUrl(orderId, amountVND, orderInfo, ipAddress);

        System.out.println("========== CHECKOUT ==========");
        System.out.println("orderId    = " + orderId);
        System.out.println("amountVND  = " + amountVND);
        System.out.println("paymentUrl = " + paymentUrl);
        System.out.println("==============================");

        response.sendRedirect(paymentUrl);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("cart.jsp");
    }
}