package com.babyshop.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "invoices")
public class Invoice implements Serializable {
    
    @Id
    @Column(name = "id")
    private String id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "employeeId")
    private Employee employee;

    @Column(name = "total")
    private double total;
    @Column(name = "payable")
    private double payable;
    @Column(name = "paid")
    private double paid;
    @Column(name = "returned")
    private double returned;
    @Column(name = "datetime")
    private String date;
    
    public Invoice() {
        
    }

    public Invoice(String id, Employee employee, double total,double payable, double paid, double returned, String date) {
        this.id = id;
        this.employee = employee;
        this.total = total;
        this.payable = payable;
        this.paid = paid;
        this.returned = returned;
        this.date = date;
    }

    public Invoice(String id, Employee employee, double total, double payable, double paid, double returned) {
        this.id = id;
        this.employee = employee;
        this.total = total;
        this.payable = payable;
        this.paid = paid;
        this.returned = returned;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPayable() {
        return payable;
    }

    public void setPayable(double payable) {
        this.payable = payable;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getReturned() {
        return returned;
    }

    public void setReturned(double returned) {
        this.returned = returned;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
