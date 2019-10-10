package com.retail.model.bill;

import java.math.BigDecimal;

import com.retail.model.user.User;
import com.retail.types.CategoryType;

/**
 * An interface for items that can be discounted
 * 
 * 
 *
 */
public interface Discountable {
    
    public BigDecimal getNetPayable();
    
    public User getUser();
    
    public CategoryType getCategory();

}
