package com.babyshop.model;

import com.babyshop.HibernateUtil;
import com.babyshop.dao.ProductDao;
import com.babyshop.entity.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class ProductModel implements ProductDao {

    private EntityManager entityManager;

    public ProductModel() {
        entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
    }

    @Override
    public ObservableList<Product> getProducts() {

        ObservableList<Product> list = FXCollections.observableArrayList();

        try {
            entityManager.getTransaction().begin();
            TypedQuery<Product> query = entityManager.createQuery("from Product", Product.class);
            List<Product> products = query.getResultList();
            list.addAll(products);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public Product getProduct(long id) {

        Product product = null;

        try {
            entityManager.getTransaction().begin();
            product = entityManager.find(Product.class, id);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }

        return product;
    }

    @Override
    public Product getProductByName(String productName) {

        Product product = null;

        try {
            entityManager.getTransaction().begin();
            TypedQuery<Product> query = entityManager.createQuery("from Product where productName = :name", Product.class);
            query.setParameter("name", productName);
            product = query.getSingleResult();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }

        return product;
    }

    @Override
    public void saveProduct(Product product) {

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(product);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void updateProduct(Product product) {

        try {
            entityManager.getTransaction().begin();
            Product p = entityManager.find(Product.class, product.getId());
            if (p != null) {
                p.setProductName(product.getProductName());
                p.setCategory(product.getCategory());
                p.setQuantity(product.getQuantity());
                p.setPrice(product.getPrice());
                p.setDescription(product.getDescription());
                entityManager.merge(p);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void increaseProduct(Product product) {

        try {
            entityManager.getTransaction().begin();
            Product p = entityManager.find(Product.class, product.getId());
            if (p != null) {
                p.setQuantity(p.getQuantity() + product.getQuantity());
                entityManager.merge(p);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void decreaseProduct(Product product) {
        try {
            entityManager.getTransaction().begin();
            Product p = entityManager.find(Product.class, product.getId());
            if (p != null) {
                p.setQuantity(product.getQuantity());
                entityManager.merge(p);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProduct(Product product) {

        try {
            entityManager.getTransaction().begin();
            Product p = entityManager.find(Product.class, product.getId());
            if (p != null) {
                entityManager.remove(p);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public ObservableList<String> getProductNames() {

        ObservableList<String> list = FXCollections.observableArrayList();

        try {
            entityManager.getTransaction().begin();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<String> cq = cb.createQuery(String.class);
            Root<Product> root = cq.from(Product.class);
            cq.select(root.get("productName"));
            TypedQuery<String> query = entityManager.createQuery(cq);
            list.addAll(query.getResultList());
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }

        return list;
    }
}
