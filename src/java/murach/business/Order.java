package murach.business;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order implements Serializable {

    @Id
    @Column(name = "order_id", length = 50, nullable = false)
    private String orderId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_address")
    private String customerAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
               fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private double total;

    @Column(length = 20, nullable = false)
    private String status;   // PENDING | PAID | FAILED

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "paid_at")
    private Date paidAt;

    @Column(name = "sepay_transaction_id", length = 100)
    private String sePayTransactionId;

    public Order() {}

    public Order(String orderId, String customerName, String customerEmail,
                 String customerPhone, String customerAddress,
                 List<LineItem> lineItems, double total) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.total = total;
        this.status = "PENDING";
        this.createdAt = new Date();

        for (LineItem li : lineItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(this);
            oi.setProductCode(li.getProduct().getCode());
            oi.setDescription(li.getProduct().getDescription());
            oi.setPrice(li.getProduct().getPrice());
            oi.setQuantity(li.getQuantity());
            this.items.add(oi);
        }
    }

    // ===== Getters & Setters =====
    public String getOrderId()                    { return orderId; }
    public void setOrderId(String v)              { this.orderId = v; }

    public String getCustomerName()               { return customerName; }
    public void setCustomerName(String v)         { this.customerName = v; }

    public String getCustomerEmail()              { return customerEmail; }
    public void setCustomerEmail(String v)        { this.customerEmail = v; }

    public String getCustomerPhone()              { return customerPhone; }
    public void setCustomerPhone(String v)        { this.customerPhone = v; }

    public String getCustomerAddress()            { return customerAddress; }
    public void setCustomerAddress(String v)      { this.customerAddress = v; }

    public List<OrderItem> getItems()             { return items; }
    public void setItems(List<OrderItem> v)       { this.items = v; }

    public double getTotal()                      { return total; }
    public void setTotal(double v)                { this.total = v; }

    public String getStatus()                     { return status; }
    public void setStatus(String v)               { this.status = v; }

    public Date getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(Date v)              { this.createdAt = v; }

    public Date getPaidAt()                       { return paidAt; }
    public void setPaidAt(Date v)                 { this.paidAt = v; }

    public String getSePayTransactionId()         { return sePayTransactionId; }
    public void setSePayTransactionId(String v)   { this.sePayTransactionId = v; }
}