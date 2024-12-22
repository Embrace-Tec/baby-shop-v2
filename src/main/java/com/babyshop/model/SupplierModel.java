package com.babyshop.model;

import com.babyshop.HibernateUtil;
import com.babyshop.dao.SupplierDao;
import com.babyshop.entity.Supplier;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class SupplierModel implements SupplierDao {

    private EntityManager entityManager;

    public SupplierModel() {
        this.entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
    }

    @Override
    public ObservableList<Supplier> getSuppliers() {
        ObservableList<Supplier> list = FXCollections.observableArrayList();

        try {
            // Begin transaction
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            // Query to fetch all suppliers
            List<Supplier> suppliers = entityManager.createQuery("from Supplier", Supplier.class).getResultList();
            list.addAll(suppliers);

            // Commit transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // Rollback in case of an error
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            // Close the EntityManager after the operation
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return list;
    }

    @Override
    public Supplier getSupplier(long id) {
        Supplier supplier = null;

        try {
            // Begin transaction
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            // Fetch the supplier by id
            supplier = entityManager.find(Supplier.class, id);

            // Commit transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }

        return supplier;
    }

    @Override
    public void saveSuplier(Supplier supplier) {
        try {
            // Begin transaction
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            // Save the supplier
            entityManager.persist(supplier);

            // Commit transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }
    }

    @Override
    public void updateSuplier(Supplier supplier) {
        try {
            // Begin transaction
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            // Find the existing supplier by ID and update it
            Supplier existingSupplier = entityManager.find(Supplier.class, supplier.getId());
            if (existingSupplier != null) {
                existingSupplier.setName(supplier.getName());
                existingSupplier.setPhone(supplier.getPhone());
                existingSupplier.setAddress(supplier.getAddress());
                entityManager.merge(existingSupplier); // Merge the changes
            }

            // Commit transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }
    }

    @Override
    public void deleteSuplier(Supplier supplier) {
        try {
            // Begin transaction
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            // Find the supplier by ID and delete it
            Supplier existingSupplier = entityManager.find(Supplier.class, supplier.getId());
            if (existingSupplier != null) {
                entityManager.remove(existingSupplier);
            }

            // Commit transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }
    }

    public ObservableList<String> getNames() {
        ObservableList<String> list = FXCollections.observableArrayList();

        try {
            // Begin transaction
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            // Criteria query to fetch supplier names
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<String> query = builder.createQuery(String.class);
            Root<Supplier> root = query.from(Supplier.class);
            query.select(root.get("name"));

            // Execute the query and populate the list
            list.addAll(entityManager.createQuery(query).getResultList());

            // Commit transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }

        return list;
    }
}
