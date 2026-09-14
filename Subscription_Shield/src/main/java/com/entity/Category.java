package com.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "description", length = 255)
    private String description;

    public Category() {
    }

    public Category(int categoryId, String name, String icon, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    public Category(String name, String icon, String description) {
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Category [ID: %d | Name: %s | Icon: %s | Description: %s]",
                categoryId, name, icon != null ? icon : "N/A", description != null ? description : "N/A");
    }
}
