package com.retail.model.discount;

import java.math.BigDecimal;

import com.retail.model.bill.Discountable;

/**
 * A common interface for retail discounts
 * 
 *
 */
public interface Discount {
    
    /**
     * Apply the discount to the provided bill
     * @param discountable - the discountable instance for which the discount should be applied
     * @return the discounted amount if applicable, null otherwise
     */
    public BigDecimal calculate(Discountable discountable);
    
    /**
     * Determines if the discount is applicable to the provided discountable item
     * @param discountable the item that can be discounted
     * @return true if the discount is applicable
     */
    public boolean isApplicable(Discountable discountable);

}
