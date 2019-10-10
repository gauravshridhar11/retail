package com.retail.service.discount;

import java.util.List;

import com.retail.model.discount.Discount;

/**
 * An interface for a discount service that supports loading
 * Discounts rules for a database or some configrations store
 * 
 *
 */
public interface DiscountService {
    
    /**
     * Load the discounts that are always applicable
     * @return always applicable discounts
     */
    List<Discount> loadAlwayApplicableDiscounts();
    
    /**
     * Load the discounts that are mutually exclusive, i.e. only one 
     * should be applied
     * @return mutually exclusive discounts
     */
    List<Discount> loadMutuallyExclusiveDiscounts();

}
