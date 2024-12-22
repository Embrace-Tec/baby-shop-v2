package com.babyshop.model;

import com.babyshop.HibernateUtil;
import com.babyshop.dao.EmployeeDao;
import com.babyshop.entity.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeModel implements EmployeeDao {

    private static final Logger logger = Logger.getLogger(EmployeeModel.class.getName());

    // Helper method to handle transactions
    private <T> T executeTransaction(SessionOperation<T> operation) {
        Session session = null;
        Transaction transaction = null;
        T result = null;
        try {
            session = HibernateUtil.getEntityManagerFactory().createEntityManager().unwrap(Session.class);  // Open session
            transaction = session.beginTransaction();  // Begin transaction
            result = operation.execute(session);  // Execute the operation
            transaction.commit();  // Commit transaction
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();  // Rollback on error
            }
            logger.log(Level.SEVERE, "Transaction failed", e);  // Log the error
        } finally {
            if (session != null) {
                session.close();  // Ensure session is closed
            }
        }
        return result;
    }

    @Override
    public ObservableList<Employee> getEmployees() {
        return executeTransaction(session -> {
            Query<Employee> query = session.createQuery("from Employee", Employee.class);
            List<Employee> employees = query.list();
            return FXCollections.observableArrayList(employees);  // Return observable list
        });
    }

    @Override
    public Employee getEmployee(long id) {
        return executeTransaction(session -> session.get(Employee.class, id));  // Retrieve employee by ID
    }

    @Override
    public String getEmployeeType(String username) {
        return executeTransaction(session -> {
            Query<Employee> query = session.createQuery("from Employee where userName = :username", Employee.class);
            query.setParameter("username", username);
            Employee employee = query.uniqueResult();
            return employee != null ? employee.getType() : null;  // Return employee type
        });
    }

    @Override
    public void saveEmployee(Employee employee) {
        executeTransaction(session -> {
            session.save(employee);  // Save employee
            return null;  // No result to return
        });
    }

    @Override
    public void updateEmployee(Employee employee) {
        executeTransaction(session -> {
            Employee existingEmployee = session.get(Employee.class, employee.getId());
            if (existingEmployee != null) {
                existingEmployee.setFirstName(employee.getFirstName());
                existingEmployee.setLastName(employee.getLastName());
                existingEmployee.setUserName(employee.getUserName());
                existingEmployee.setPassword(employee.getPassword());
                existingEmployee.setPhone(employee.getPhone());
                existingEmployee.setAddress(employee.getAddress());
                logger.info("Employee updated: " + existingEmployee.getUserName());
            } else {
                logger.warning("Employee not found for update: " + employee.getId());
            }
            return null;  // No result to return
        });
    }

    @Override
    public void deleteEmployee(Employee employee) {
        executeTransaction(session -> {
            Employee existingEmployee = session.get(Employee.class, employee.getId());
            if (existingEmployee != null) {
                session.delete(existingEmployee);  // Delete the employee
                logger.info("Employee deleted: " + existingEmployee.getUserName());
            } else {
                logger.warning("Employee not found for deletion: " + employee.getId());
            }
            return null;  // No result to return
        });
    }

    @Override
    public boolean checkUser(String username) {
        return executeTransaction(session -> {
            Query<Employee> query = session.createQuery("from Employee where userName = :username", Employee.class);
            query.setParameter("username", username);
            Employee employee = query.uniqueResult();
            return employee != null;  // Return true if employee exists
        });
    }

    @Override
    public boolean checkPassword(String username, String password) {
        return executeTransaction(session -> {
            Query<Employee> query = session.createQuery("from Employee where userName = :username", Employee.class);
            query.setParameter("username", username);
            Employee employee = query.uniqueResult();
            return employee != null && employee.getPassword().equals(password);  // Return true if password matches
        });
    }

    // Functional interface to execute operations within a transaction
    @FunctionalInterface
    private interface SessionOperation<T> {
        T execute(Session session);
    }
}
