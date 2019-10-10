package com.retail.service.discount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.retail.model.discount.CustomerPeriodDiscount;
import com.retail.model.discount.Discount;
import com.retail.model.discount.NetMultiplesDiscount;
import com.retail.model.discount.UserTypeDiscount;
import com.retail.types.CategoryType;
import com.retail.types.DiscountType;
import com.retail.types.UserType;

/**
 * Implementation of DiscountService. The discounts are hardcoded here
 * but in real life may be loaded from a database or an external store
 * 
 *
 */
public class DiscountServiceImpl implements DiscountService {
    
    // discounts that are always applied
    private List<Discount> alwaysApplicable = new ArrayList<>();
    
    // discounts that only one is applied
    private List<Discount> mutuallyExclusive = new ArrayList<>();
    
    /**
     * Constructs a new service
     */
    public DiscountServiceImpl() {
        super();
        
        // discounts that are always applicable
        alwaysApplicable = new ArrayList<>();
        //For every $100 on the bill, there would be a $5 discount 
        Discount discount = new NetMultiplesDiscount(new BigDecimal(5), null, 
                new BigDecimal(100));
        
        alwaysApplicable.add(discount);
        
        // mutually exclusive discounts
        // A user can get only one of the percentage based discounts on a bill
        // The percentage based discounts do not apply on groceries. 
        Set<CategoryType> exclude = new HashSet<>();
        exclude.add(CategoryType.GROCERIES);
        
        
        // The discounts below are applied in the order they are inserted
        // If the user is an employee of the store, he gets a 30% discount 
        discount = new UserTypeDiscount(DiscountType.PERCENTAGE, 
                new BigDecimal(30), exclude, UserType.EMPLOYEE);
        mutuallyExclusive.add(discount);
        
        // If the user is an affiliate of the store, he gets a 10% discount
        discount = new UserTypeDiscount(DiscountType.PERCENTAGE, 
                new BigDecimal(10), exclude, UserType.AFFILIATE);
        mutuallyExclusive.add(discount);
        
        // If the user has been a customer for over 2 years (24 months), he gets a 5% discount
        discount = new CustomerPeriodDiscount(DiscountType.PERCENTAGE, 
                new BigDecimal(5), exclude, 24);
        mutuallyExclusive.add(discount);
        
    }

    @Override
    public List<Discount> loadAlwayApplicableDiscounts() {
        
        return Collections.unmodifiableList(alwaysApplicable);
    }

    @Override
    public List<Discount> loadMutuallyExclusiveDiscounts() {
        
        return Collections.unmodifiableList(mutuallyExclusive);
    }

}
