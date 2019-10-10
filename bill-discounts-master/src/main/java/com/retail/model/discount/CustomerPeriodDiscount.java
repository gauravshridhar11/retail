package com.retail.model.discount;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import com.retail.model.bill.Discountable;
import com.retail.types.CategoryType;
import com.retail.types.DiscountType;

/**
 * A discount based on how long the user has been a customer
 * 
 * 
 *
 */
public class CustomerPeriodDiscount extends GenericDiscount {
    
    private Integer months;
    

    /**
     * Create discount
     * @param type percentage or amount, defaults to percentage
     * @param discount the value of the discount either an actual amount or percentage
     * @param exclude the excluded categories
     */
    public CustomerPeriodDiscount(DiscountType type, BigDecimal discount, Set<CategoryType> exclude,
            Integer months) {
        
        super(type, discount, exclude);
        if(months == null) {
            throw new IllegalArgumentException("months is required");
        }
        this.months = months;
    }

    @Override
    public boolean isApplicable(Discountable discountable) {
        
        if((discountable == null) || (discountable.getUser() == null) 
                || (discountable.getUser().getCustomerSince() == null)) {
            throw new IllegalArgumentException("discountable is missing or invalid");
        }
        
        if(months == null) {
            throw new IllegalArgumentException("months is required");
        }
        
        // check if the category is excluded
        boolean applicable = super.isCategoryApplicable(discountable.getCategory());
        
        if(applicable) {
            
            // check if the number of months on this instance is smaller than the time the user
            // has been a customer
            // get a calendar date x months ago
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -months);
            // start of the day
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            Date customerSince = discountable.getUser().getCustomerSince();

            applicable = customerSince.before(calendar.getTime());

        }

        return applicable;
    }


    /**
     * @return the months
     */
    public Integer getMonths() {
        return months;
    }


    /**
     * @param months the months to set
     */
    public void setMonths(Integer months) {
        this.months = months;
    }

}
