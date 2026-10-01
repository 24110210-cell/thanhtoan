package murach.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import murach.business.Order;
import java.util.Date;
import java.util.List;

public class OrderDB {

    /** Lưu Order mới vào DB (kèm cascade OrderItem) */
    public static void insert(Order order) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(order);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /** Cập nhật Order */
    public static void update(Order order) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(order);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /** Tìm Order theo orderId */
    public static Order selectOrder(String orderId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Order.class, orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /** Lấy tất cả Order */
    public static List<Order> selectAll() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT o FROM Order o ORDER BY o.createdAt DESC";
            TypedQuery<Order> q = em.createQuery(qString, Order.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /** Cập nhật trạng thái nhanh theo orderId */
    public static void updateStatus(String orderId, String status,
                                    String sePayTransactionId) {
        Order o = selectOrder(orderId);
        if (o != null) {
            o.setStatus(status);
            if ("PAID".equals(status)) {
                o.setPaidAt(new Date());
            }
            if (sePayTransactionId != null && !sePayTransactionId.isEmpty()) {
                o.setSePayTransactionId(sePayTransactionId);
            }
            update(o);
        }
    }

    /** ⭐ Tìm đơn hàng PENDING mới nhất — dùng khi SePay không gửi orderId về */
    public static Order findLatestPending() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT o FROM Order o WHERE o.status = 'PENDING' "
                           + "ORDER BY o.createdAt DESC";
            TypedQuery<Order> q = em.createQuery(qString, Order.class);
            q.setMaxResults(1);
            List<Order> result = q.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
}