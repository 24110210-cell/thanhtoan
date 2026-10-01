package murach.data;

import murach.business.Product;
import java.util.HashMap;
import java.util.Map;

public class ProductIO {

    private static final Map<String, Product> PRODUCTS = new HashMap<>();

    static {
        PRODUCTS.put("8601", new Product("8601", "86 (the band) - True Life Songs and Pictures", 14.95));
        PRODUCTS.put("pf01", new Product("pf01", "Paddlefoot - The first CD", 12.95));
        PRODUCTS.put("pf02", new Product("pf02", "Paddlefoot - The second CD", 14.95));
        PRODUCTS.put("jr01", new Product("jr01", "Joe Rut - Genuine Wood Grained Finish", 14.95));
    }

    public static Product getProduct(String productCode) {
        return PRODUCTS.get(productCode);
    }
}