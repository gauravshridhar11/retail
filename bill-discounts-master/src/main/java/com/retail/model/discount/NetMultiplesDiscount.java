package com.retail.model.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import com.retail.model.bill.Discountable;
import com.retail.types.CategoryType;
import com.retail.types.DiscountType;

/**
 * A discount based on a certain amount off a net multiples.
 * e.g. $5 off for every $100
 * 
 * 
 *
 */
public class NetMultiplesDiscount extends GenericDiscount {
    
    private BigDecimal netMultiples;
    

    /**
     * Create a new net multiples discount
     * @param discount the value of the discount either an actual amount or percentage
     * @param exclude the excluded categories
     * @param netMultiples the multiples from the net on which the discount applies
     */
    public NetMultiplesDiscount(BigDecimal discount, Set<CategoryType> exclude, 
            BigDecimal netMultiples) {
        
        super(DiscountType.AMOUNT, discount, exclude);
        
        if((netMultiples == null) || BigDecimal.ZERO.equals(netMultiples)) {
            throw new IllegalArgumentException("netMultiples is missing or invalid");
        }
        
        this.netMultiples = netMultiples;
        
    }

    @Override
    public boolean isApplicable(Discountable discountable) {
        
        validate(discountable);
        
        if(netMultiples == null) {
            throw new IllegalArgumentException("netMultiples is required");
        }
        
        // check if the category is excluded
        boolean applicable = super.isCategoryApplicable(discountable.getCategory());
        
        if(applicable) {

            int compare = discountable.getNetPayable().compareTo(netMultiples);

            // only applicable if the netPayable is equal to or greater than the netMultiples
            applicable = (compare == 0) || (compare == 1);

        } 
        
        return applicable;
    }
    
    /**
     * Net multiples overrides the GenericDiscount calculate method 
     */
    @Override
    public BigDecimal calculate(Discountable discountable) {
        BigDecimal amount = null;
        
        validate(discountable);
        
        if(getDiscount() == null) {
            throw new IllegalArgumentException("discount is null");
        }
        
        if(this.isApplicable(discountable)) {
            
            amount = discountable.getNetPayable().divide(netMultiples, 0, RoundingMode.FLOOR);
            
            amount = amount.multiply(getDiscount()).setScale(2, RoundingMode.HALF_UP);;

        }
        
        return amount;
    }
    
    /**
     * @return the netMultiples
     */
    public BigDecimal getNetMultiples() {
        return netMultiples;
    }


    /**
     * @param netMultiples the netMultiples to set
     */
    public void setNetMultiples(BigDecimal netMultiples) {
        this.netMultiples = netMultiples;
    }


}
