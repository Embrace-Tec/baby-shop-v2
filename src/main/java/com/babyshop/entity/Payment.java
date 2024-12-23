package com.babyshop.entity;

public class Payment {
    
    private double subTotal;
    private double payable;

    public Payment(double subTotal,double payable) {
        this.subTotal = subTotal;
        this.payable = payable;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getPayable() {
        return payable;
    }

    public void setPayable(double payable) {
        this.payable = payable;
    }

    
    
}
