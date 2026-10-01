package murach.business;

import java.io.Serializable;

public final class Product implements Serializable {

    private final String code;
    private final String description;
    private final double price;

    public Product(String code, String description, double price) {
        this.code = code;
        this.description = description;
        this.price = price;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
}