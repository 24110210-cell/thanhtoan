package murach.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class VNPayUtil {

    // ⭐ Credentials từ email VNPay Sandbox
    public static final String VNP_TMN_CODE    = "E4PMHK5A";
    public static final String VNP_HASH_SECRET = "DIXXJNQMRRGOHJSAGBLPGJTHHOCFCPZH";

    public static final String VNP_URL        = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String VNP_RETURN_URL = "http://localhost:8080/GioHang/vnpay-return";
    public static final String VNP_VERSION    = "2.1.0";
    public static final String VNP_COMMAND    = "pay";

    /** Tạo chữ ký HMAC-SHA512 */
    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo HMAC-SHA512", e);
        }
    }

    /** Tạo URL thanh toán VNPay */
    public static String createPaymentUrl(String orderId, long amountVND,
                                          String orderInfo, String ipAddress) {
        Map<String, String> params = new TreeMap<>();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        params.put("vnp_Version",    VNP_VERSION);
        params.put("vnp_Command",    VNP_COMMAND);
        params.put("vnp_TmnCode",    VNP_TMN_CODE);
        params.put("vnp_Amount",     String.valueOf(amountVND * 100)); // x100 theo chuẩn VNPay
        params.put("vnp_CurrCode",   "VND");
        params.put("vnp_TxnRef",     orderId);
        params.put("vnp_OrderInfo",  orderInfo);
        params.put("vnp_OrderType",  "other");
        params.put("vnp_Locale",     "vn");
        params.put("vnp_ReturnUrl",  VNP_RETURN_URL);
        params.put("vnp_IpAddr",     ipAddress);
        params.put("vnp_CreateDate", fmt.format(cal.getTime()));

        StringBuilder hashData = new StringBuilder();
        StringBuilder query    = new StringBuilder();
        for (Map.Entry<String, String> e : params.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            if (v == null || v.isEmpty()) continue;
            hashData.append(k).append('=')
                    .append(URLEncoder.encode(v, StandardCharsets.US_ASCII))
                    .append('&');
            query.append(URLEncoder.encode(k, StandardCharsets.US_ASCII))
                 .append('=')
                 .append(URLEncoder.encode(v, StandardCharsets.US_ASCII))
                 .append('&');
        }
        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        String secureHash = hmacSHA512(VNP_HASH_SECRET, hashData.toString());
        return VNP_URL + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    /** Xác thực chữ ký từ callback VNPay */
    public static boolean verifySignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null) return false;

        Map<String, String> data = new TreeMap<>(params);
        data.remove("vnp_SecureHash");
        data.remove("vnp_SecureHashType");

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> e : data.entrySet()) {
            if (e.getValue() == null || e.getValue().isEmpty()) continue;
            hashData.append(e.getKey()).append('=')
                    .append(URLEncoder.encode(e.getValue(), StandardCharsets.US_ASCII))
                    .append('&');
        }
        hashData.setLength(hashData.length() - 1);

        String expected = hmacSHA512(VNP_HASH_SECRET, hashData.toString());
        return expected.equalsIgnoreCase(receivedHash);
    }
}