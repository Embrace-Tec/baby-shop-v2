package com.babyshop.model;

import com.babyshop.HibernateUtil;
import com.babyshop.dao.InvoiceDao;
import com.babyshop.entity.Invoice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class InvoiceModel implements InvoiceDao {

    private EntityManager entityManager;

    public InvoiceModel() {
        // Get the EntityManager from HibernateUtil
        this.entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
    }

    @Override
    public ObservableList<Invoice> getInvoices() {
        ObservableList<Invoice> list = FXCollections.observableArrayList();

        try {
            // Begin transaction
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            // Query to fetch all invoices
            List<Invoice> invoices = entityManager.createQuery("from Invoice", Invoice.class).getResultList();
            list.addAll(invoices);

            // Commit transaction
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    @Override
    public Invoice getInvoice(String id) {
        Invoice invoice = null;

        try {
            // Begin transaction
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            // Fetch the invoice by id
            invoice = entityManager.find(Invoice.class, id);

            // Commit transaction
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return invoice;
    }

    @Override
    public void saveInvoice(Invoice invoice) {
        try {
            // Begin transaction
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            // Save the invoice
            entityManager.persist(invoice);

            // Commit transaction
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void deleteCategory(Invoice invoice) {
        try {
            // Begin transaction
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            // Get the invoice by id and delete it
            Invoice i = entityManager.find(Invoice.class, invoice.getId());
            if (i != null) {
                entityManager.remove(i);
            }

            // Commit transaction
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
