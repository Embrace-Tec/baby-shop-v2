package com.babyshop.model;

import com.babyshop.HibernateUtil;
import com.babyshop.dao.PurchaseDao;
import com.babyshop.entity.Purchase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class PurchaseModel implements PurchaseDao {

    // Helper method to handle transactions
    private void executeTransaction(SessionOperation operation) {
        Session session = HibernateUtil.getEntityManagerFactory().createEntityManager().unwrap(Session.class);
        try {
            session.beginTransaction();  // Begin transaction
            operation.execute(session);  // Execute the operation
            session.getTransaction().commit();  // Commit transaction
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();  // Rollback on error
            }
            e.printStackTrace();  // Log the error
        } finally {
            session.close();  // Close session after transaction
        }
    }

    @Override
    public ObservableList<Purchase> getPurchases() {
        ObservableList<Purchase> list = FXCollections.observableArrayList();
        executeTransaction(session -> {
            Query<Purchase> query = session.createQuery("from Purchase", Purchase.class);
            List<Purchase> purchases = query.list();
            list.addAll(purchases);  // Efficiently add all purchases to the list
        });
        return list;
    }

    @Override
    public Purchase getPurchase(long id) {
        final Purchase[] purchase = {null};
        executeTransaction(session -> {
            purchase[0] = session.get(Purchase.class, id);  // Retrieve the purchase
        });
        return purchase[0];
    }

    @Override
    public void savePurchase(Purchase purchase) {
        executeTransaction(session -> session.save(purchase));  // Save the purchase
    }

    @Override
    public void updatePurchase(Purchase purchase) {
        executeTransaction(session -> {
            Purchase p = session.get(Purchase.class, purchase.getId());
            if (p != null) {
                p.setProduct(purchase.getProduct());
                p.setSupplier(purchase.getSupplier());
                p.setQuantity(purchase.getQuantity());
                p.setDate(purchase.getDate());
            }
        });
    }

    @Override
    public void deletePurchase(Purchase purchase) {
        executeTransaction(session -> {
            Purchase p = session.get(Purchase.class, purchase.getId());
            if (p != null) {
                session.delete(p);  // Delete the purchase
            }
        });
    }

    // Functional interface to execute operations within a transaction
    @FunctionalInterface
    private interface SessionOperation {
        void execute(Session session);
    }
}
