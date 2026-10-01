package murach.controllers;

import murach.business.Cart;
import murach.business.Order;
import murach.business.OrderItem;
import murach.data.OrderDB;
import murach.util.MailUtilGmail;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@WebServlet("/vnpay-return")
public class VNPayReturnServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // 1. Lấy tất cả tham số VNPay gửi về
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0) params.put(k, v[0]);
        });

        String responseCode  = params.get("vnp_ResponseCode");
        String txnRef        = params.get("vnp_TxnRef");         // orderId
        String transactionNo = params.get("vnp_TransactionNo");

        // Fallback: nếu VNPay không gửi txnRef, lấy từ session
        if (txnRef == null || txnRef.isEmpty()) {
            txnRef = (String) session.getAttribute("pendingOrderId");
        }

        System.out.println("========== VNPAY RETURN ==========");
        System.out.println("responseCode = " + responseCode);
        System.out.println("txnRef       = " + txnRef);
        System.out.println("transactionNo= " + transactionNo);
        System.out.println("==================================");

        Order order = (txnRef != null) ? OrderDB.selectOrder(txnRef) : null;
        if (order == null) {
            order = OrderDB.findLatestPending();
            if (order != null) txnRef = order.getOrderId();
        }

        // "00" là mã thành công của VNPay
        if ("00".equals(responseCode) && order != null) {
            // ===== THÀNH CÔNG =====
            OrderDB.updateStatus(txnRef, "PAID", transactionNo);
            order = OrderDB.selectOrder(txnRef);

            Cart cart = (Cart) session.getAttribute("cart");
            if (cart != null) cart.clear();
            session.removeAttribute("pendingOrderId");

            // ⭐ Gửi mail xác nhận
            try {
                sendPaymentConfirmationMail(order);
                System.out.println(">>> Đã gửi mail xác nhận tới: " + order.getCustomerEmail());
            } catch (Exception e) {
                log("Lỗi gửi mail: " + e.getMessage());
                e.printStackTrace();
            }

            response.sendRedirect("index.jsp?msg=paid&orderId="
                    + URLEncoder.encode(txnRef, StandardCharsets.UTF_8));
        } else {
            // ===== THẤT BẠI =====
            if (order != null) {
                OrderDB.updateStatus(txnRef, "FAILED", null);
            }
            response.sendRedirect("index.jsp?msg=failed");
        }
    }

    /** Gửi mail xác nhận thanh toán qua Brevo */
    private void sendPaymentConfirmationMail(Order order) {
        String to      = order.getCustomerEmail();
        String from    = "24110210@student.hcmute.edu.vn";
        String subject = "✅ Xác nhận thanh toán thành công - Đơn hàng " + order.getOrderId();

        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

        StringBuilder itemsHtml = new StringBuilder();
        itemsHtml.append("<table style='width:100%; border-collapse:collapse; margin-top:10px;'>")
                 .append("<thead><tr style='background:#f0f0f0;'>")
                 .append("<th style='padding:8px; border:1px solid #ddd; text-align:left;'>Sản phẩm</th>")
                 .append("<th style='padding:8px; border:1px solid #ddd;'>SL</th>")
                 .append("<th style='padding:8px; border:1px solid #ddd;'>Đơn giá</th>")
                 .append("<th style='padding:8px; border:1px solid #ddd;'>Thành tiền</th>")
                 .append("</tr></thead><tbody>");

        for (OrderItem item : order.getItems()) {
            itemsHtml.append("<tr>")
                     .append("<td style='padding:8px; border:1px solid #ddd;'>")
                     .append(item.getDescription()).append("</td>")
                     .append("<td style='padding:8px; border:1px solid #ddd; text-align:center;'>")
                     .append(item.getQuantity()).append("</td>")
                     .append("<td style='padding:8px; border:1px solid #ddd; text-align:right;'>")
                     .append(currency.format(item.getPrice())).append("</td>")
                     .append("<td style='padding:8px; border:1px solid #ddd; text-align:right;'>")
                     .append(currency.format(item.getSubtotal())).append("</td>")
                     .append("</tr>");
        }
        itemsHtml.append("</tbody></table>");

        String body = ""
                + "<!DOCTYPE html><html><head><meta charset='UTF-8'></head>"
                + "<body style='margin:0; padding:0; background:#f4f4f4; "
                + "font-family:Arial,sans-serif;'>"
                + "<div style='max-width:600px; margin:20px auto; background:#fff; "
                + "border-radius:8px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);'>"

                + "<div style='background:#27ae60; color:#fff; padding:25px; text-align:center;'>"
                + "<h1 style='margin:0; font-size:24px;'>✅ Thanh toán thành công!</h1>"
                + "<p style='margin:8px 0 0 0; font-size:14px;'>"
                + "Cảm ơn bạn đã đặt hàng tại CD Store</p>"
                + "</div>"

                + "<div style='padding:30px; color:#333; line-height:1.6;'>"
                + "<p>Xin chào <strong>" + order.getCustomerName() + "</strong>,</p>"
                + "<p>Chúng tôi đã nhận được thanh toán cho đơn hàng "
                + "<strong>" + order.getOrderId() + "</strong>.</p>"

                + "<h3 style='color:#27ae60; margin-top:25px;'>Chi tiết đơn hàng</h3>"
                + "<p><strong>Mã đơn hàng:</strong> " + order.getOrderId() + "<br>"
                + "<strong>Ngày thanh toán:</strong> " + order.getPaidAt() + "<br>"
                + "<strong>Mã giao dịch:</strong> "
                + (order.getSePayTransactionId() != null ? order.getSePayTransactionId() : "N/A")
                + "</p>"

                + "<h3 style='color:#27ae60; margin-top:25px;'>Sản phẩm đã mua</h3>"
                + itemsHtml.toString()

                + "<p style='text-align:right; font-size:16px; margin-top:15px;'>"
                + "<strong>Tổng cộng: "
                + "<span style='color:#27ae60; font-size:18px;'>"
                + currency.format(order.getTotal()) + "</span></strong></p>"

                + "<h3 style='color:#27ae60; margin-top:25px;'>Địa chỉ giao hàng</h3>"
                + "<p>" + order.getCustomerName() + "<br>"
                + "SĐT: " + order.getCustomerPhone() + "<br>"
                + order.getCustomerAddress() + "</p>"

                + "<p>Trân trọng,<br>"
                + "<strong>Đội ngũ CD Store</strong></p>"
                + "</div>"

                + "<div style='background:#f0f0f0; color:#777; padding:15px; "
                + "text-align:center; font-size:12px;'>"
                + "Email này được gửi tự động sau khi bạn thanh toán thành công."
                + "</div>"

                + "</div></body></html>";

        MailUtilGmail.sendMail(to, from, subject, body, true);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}