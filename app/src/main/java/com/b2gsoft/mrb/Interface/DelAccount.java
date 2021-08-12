package com.b2gsoft.mrb.Interface;

import com.b2gsoft.mrb.Model.Category;
import com.b2gsoft.mrb.Model.Customer;
import com.b2gsoft.mrb.Model.SubCategory;

public interface DelAccount {

    public void Delete(String id);
    public void DeleteCategory(Category category);
    public void DeleteSubCategory(SubCategory subCategory);
    public void DeleteCustomer(Customer customer);
}
