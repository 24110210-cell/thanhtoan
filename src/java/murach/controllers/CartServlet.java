package murach.controllers;

import murach.business.Cart;
import murach.business.LineItem;
import murach.business.Product;
import murach.data.ProductIO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

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
            Product product = ProductIO.getProduct(productCode);
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

        } else if (action.equals("shop")) {
            url = "/index.jsp";
        }

        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}