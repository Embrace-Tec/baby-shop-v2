package com.babyshop.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "categories")
public class Category implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "type")
    private String type;
    @Column(name = "description")
    private String description;
    
    public Category(){
    
    }
    
    public Category(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public Category(long id, String type, String description) {
        this.id = id;
        this.type = type;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
