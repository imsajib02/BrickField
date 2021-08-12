package com.b2gsoft.mrb.Interface;

import com.b2gsoft.mrb.Model.Category;
import com.b2gsoft.mrb.Model.Customer;
import com.b2gsoft.mrb.Model.SubCategory;


public interface Update {

    void updateCustomer(Customer customer);
    void updateCategory(Category category);
    void updateSubCategory(SubCategory subCategory);
}
