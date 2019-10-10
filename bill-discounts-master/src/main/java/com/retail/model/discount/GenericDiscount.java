package com.retail.model.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import com.retail.model.bill.Discountable;
import com.retail.types.CategoryType;
import com.retail.types.DiscountType;

/**
 * A generic discount that can be applied as either a percentage or
 * a fixed amount. The discount includes a list of CategoryTypes to 
 * exclude.
 * 
 * 
 *
 */
public abstract class GenericDiscount implements Discount {
    
    private DiscountType type;
    
    private BigDecimal discount;
    
    private Set<CategoryType> exclude;
    
    
    /**
     * @param type percentage or amount, defaults to percentage
     * @param discount the value of the discount either an actual amount or percentage
     * @param exclude the excluded categories
     */
    public GenericDiscount(DiscountType type, BigDecimal discount, Set<CategoryType> exclude) {
        super();
        
        if(discount == null) {
            throw new IllegalArgumentException("discount is required");
        }
        
        this.type = type;
        this.discount = discount;
        this.exclude = exclude;
        
        // default to percentage
        if(this.type == null) {
            this.type = DiscountType.PERCENTAGE;
        }
        
    }
    
    /**
     * Determines if the current discount is applicable for the provided
     * category
     * @param category to check in the excluded categories set
     * @return true if the category is not excluded, false otherwise
     */
    protected boolean isCategoryApplicable(CategoryType category) {
        return (exclude == null) || (category == null) || !exclude.contains(category) ;
    }
    
    /**
     * Validate the discountable
     * @param discountable to validate
     */
    protected void validate(Discountable discountable) {
        if((discountable == null) || (discountable.getNetPayable() == null)) {
            throw new IllegalArgumentException("discountable is missing or invalid");
        }
    }
    

    @Override
    public BigDecimal calculate(Discountable discountable) {
        BigDecimal amount = null;
        
        validate(discountable);
        
        if(discount == null) {
            throw new IllegalArgumentException("discount is null");
        }
        
        if(this.isApplicable(discountable)) {
            BigDecimal net = discountable.getNetPayable();
            
            if(DiscountType.PERCENTAGE.equals(type)) {
                // percentage based
                
                amount = discount.divide(new BigDecimal(100.00), 2, RoundingMode.HALF_UP).multiply(net);
                
            } else if(DiscountType.AMOUNT.equals(type)) {
                // amount based 
                
                amount = discount;
            
            } else {
                throw new IllegalArgumentException("invalid discountType: " + type);
            }
            
            amount = amount.setScale(2, RoundingMode.HALF_UP);
            
        }
        
        return amount;
    }
    
    /**
     * @return the discount amount
     * 
     */
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * @param discount, if percentage then this should be provided as a whole number i.e. 50 for 50%
     */
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * @return the exclude Set
     */
    public Set<CategoryType> getExclude() {
        return exclude;
    }

    /**
     * @param exclude the set of excluded CategoryTypes
     */
    public void setExclude(Set<CategoryType> exclude) {
        this.exclude = exclude;
    }

    /**
     * @return the type of the discount
     */
    public DiscountType getType() {
        return type;
    }

    /**
     * @param type the type of the discount
     */
    public void setType(DiscountType type) {
        this.type = type;
    }
    
    
    

}
