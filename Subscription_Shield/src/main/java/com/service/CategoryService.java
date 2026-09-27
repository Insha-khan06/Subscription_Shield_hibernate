package com.service;

import com.entity.Category;

import java.util.List;

public interface CategoryService {

    boolean addCategory(Category category);

    List<Category> getAllCategories();

    Category getCategoryById(int id);

    boolean updateCategory(Category category);

    boolean deleteCategory(int id);
}
