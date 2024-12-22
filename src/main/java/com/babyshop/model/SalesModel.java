package com.babyshop.model;

import com.babyshop.HibernateUtil;
import com.babyshop.dao.SaleDao;
import com.babyshop.entity.Sale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class SalesModel implements SaleDao {

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
    public ObservableList<Sale> getSales() {
        ObservableList<Sale> list = FXCollections.observableArrayList();
        executeTransaction(session -> {
            Query<Sale> query = session.createQuery("from Sale", Sale.class);
            List<Sale> sales = query.list();
            list.addAll(sales);  // Efficiently add all sales to the list
        });
        return list;
    }

    @Override
    public ObservableList<Sale> getSaleByProductId(long id) {
        ObservableList<Sale> list = FXCollections.observableArrayList();
        executeTransaction(session -> {
            Query<Sale> query = session.createQuery("from Sale s where s.product.id = :productId", Sale.class);
            query.setParameter("productId", id);
            List<Sale> sales = query.list();
            list.addAll(sales);  // Efficiently add all sales to the list
        });
        return list;
    }

    @Override
    public Sale getSale(long id) {
        final Sale[] sale = {null};
        executeTransaction(session -> {
            sale[0] = session.get(Sale.class, id);  // Retrieve the sale
        });
        return sale[0];
    }

    @Override
    public void saveSale(Sale sale) {
        executeTransaction(session -> session.save(sale));  // Save the sale
    }

    @Override
    public void updateSale(Sale sale) {
        executeTransaction(session -> {
            Sale s = session.get(Sale.class, sale.getId());
            if (s != null) {
                s.setProduct(sale.getProduct());
                s.setQuantity(sale.getQuantity());
                s.setPrice(sale.getPrice());
                s.setTotal(sale.getTotal());
                s.setDate(sale.getDate());
            }
        });
    }

    @Override
    public void deleteSale(Sale sale) {
        executeTransaction(session -> {
            Sale s = session.get(Sale.class, sale.getId());
            if (s != null) {
                session.delete(s);  // Delete the sale
            }
        });
    }

    // Functional interface to execute operations within a transaction
    @FunctionalInterface
    private interface SessionOperation {
        void execute(Session session);
    }
}
