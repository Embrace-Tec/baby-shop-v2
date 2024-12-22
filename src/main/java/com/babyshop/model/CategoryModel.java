package com.babyshop.model;


import com.babyshop.HibernateUtil;
import com.babyshop.dao.CategoryDao;
import com.babyshop.entity.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

public class CategoryModel implements CategoryDao {

    @Override
    public ObservableList<Category> getCategories() {
        return executeTransaction(entityManager -> {
            Query query = entityManager.createQuery("from Category", Category.class);
            List<Category> categories = query.getResultList();
            return FXCollections.observableArrayList(categories);
        });
    }

    @Override
    public Category getCategory(long id) {
        return executeTransaction(entityManager -> entityManager.find(Category.class, id));
    }

    @Override
    public void saveCategory(Category category) {
        executeTransaction(entityManager -> {
            entityManager.persist(category);
            return null;
        });
    }

    @Override
    public void updateCategory(Category category) {
        executeTransaction(entityManager -> {
            Category existingCategory = entityManager.find(Category.class, category.getId());
            if (existingCategory != null) {
                existingCategory.setType(category.getType());
                existingCategory.setDescription(category.getDescription());
            }
            return null;
        });
    }

    @Override
    public void deleteCategory(Category category) {
        executeTransaction(entityManager -> {
            Category existingCategory = entityManager.find(Category.class, category.getId());
            if (existingCategory != null) {
                entityManager.remove(existingCategory);
            }
            return null;
        });
    }

    @Override
    public ObservableList<String> getTypes() {
        return executeTransaction(entityManager -> {
            Query query = entityManager.createQuery("select distinct type from Category", String.class);
            List<String> types = query.getResultList();
            return FXCollections.observableArrayList(types);
        });
    }

    // Helper method to handle EntityManager and transaction management
    private <T> T executeTransaction(EntityManagerOperation<T> operation) {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        T result = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();  // Create EntityManager
            transaction = entityManager.getTransaction();  // Get transaction
            transaction.begin();  // Begin transaction
            result = operation.execute(entityManager);  // Execute the operation
            transaction.commit();  // Commit transaction
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();  // Rollback on error
            }
            e.printStackTrace();  // Print exception details (basic error handling)
        } finally {
            if (entityManager != null) {
                entityManager.close();  // Ensure EntityManager is closed
            }
        }
        return result;
    }

    // Functional interface to execute operations within a transaction
    @FunctionalInterface
    private interface EntityManagerOperation<T> {
        T execute(EntityManager entityManager);
    }
}
