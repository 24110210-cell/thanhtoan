package murach.business;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cart implements Serializable {
    private final List<LineItem> items = new CopyOnWriteArrayList<>();

    public Cart() {}

    public List<LineItem> getItems() {
        return items;
    }

    public synchronized void addItem(LineItem item) {
        String code = item.getProduct().getCode();
        int quantity = item.getQuantity();

        for (LineItem lineItem : items) {
            if (lineItem.getProduct().getCode().equalsIgnoreCase(code)) {
                lineItem.setQuantity(lineItem.getQuantity() + quantity);
                return;
            }
        }
        items.add(item);
    }

    public synchronized void updateItem(String code, int quantity) {
        for (LineItem lineItem : items) {
            if (lineItem.getProduct().getCode().equalsIgnoreCase(code)) {
                if (quantity > 0) {
                    lineItem.setQuantity(quantity);
                } else {
                    items.remove(lineItem);
                }
                return;
            }
        }
    }

    public synchronized void removeItem(String code) {
        items.removeIf(item -> item.getProduct().getCode().equalsIgnoreCase(code));
    }
}