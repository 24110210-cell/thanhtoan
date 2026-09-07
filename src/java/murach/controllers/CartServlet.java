package murach.controllers;

import murach.business.Cart;
import murach.business.LineItem;
import murach.business.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final Map<String, Product> PRODUCT_DATA = new HashMap<>();

    static {
        PRODUCT_DATA.put("8601", new Product("8601", "86 (the band) - True Life Songs and Pictures", 14.95));
        PRODUCT_DATA.put("pf01", new Product("pf01", "Paddlefoot - The first CD", 12.95));
        PRODUCT_DATA.put("pf02", new Product("pf02", "Paddlefoot - The second CD", 14.95));
        PRODUCT_DATA.put("jr01", new Product("jr01", "Joe Rut - Genuine Wood Grained Finish", 14.95));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "cart";
        }

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        String url = "/cart.jsp";

        if (action.equals("add")) {
            String productCode = request.getParameter("productCode");
            Product product = PRODUCT_DATA.get(productCode);
            if (product != null) {
                cart.addItem(new LineItem(product, 1));
            }
        } else if (action.equals("update")) {
            String productCode = request.getParameter("productCode");
            int quantity;
            try {
                quantity = Integer.parseInt(request.getParameter("quantity"));
            } catch (NumberFormatException e) {
                quantity = 1;
            }
            cart.updateItem(productCode, quantity);
        } else if (action.equals("remove")) {
            String productCode = request.getParameter("productCode");
            cart.removeItem(productCode);
        } else if (action.equals("checkout")) {
            url = "/checkout.jsp";
        }

        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}