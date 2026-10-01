package murach.business;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "order_items")
public class OrderItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_code", length = 20, nullable = false)
    private String productCode;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int quantity;

    public OrderItem() {}

    public Long getId()                     { return id; }
    public void setId(Long v)               { this.id = v; }

    public Order getOrder()                 { return order; }
    public void setOrder(Order v)           { this.order = v; }

    public String getProductCode()          { return productCode; }
    public void setProductCode(String v)    { this.productCode = v; }

    public String getDescription()          { return description; }
    public void setDescription(String v)    { this.description = v; }

    public double getPrice()                { return price; }
    public void setPrice(double v)          { this.price = v; }

    public int getQuantity()                { return quantity; }
    public void setQuantity(int v)          { this.quantity = v; }

    public double getSubtotal() {
        return price * quantity;
    }
}