package com.retail.model.bill;

import java.math.BigDecimal;
import java.util.List;

import com.retail.model.discount.Discount;
import com.retail.model.user.User;
import com.retail.types.CategoryType;

/**
 * Represent a user bill on the retail website
 * The bill can have discounts attached to it, the discounts
 * are managed separately. A user can have many bills, but a 
 * bill reference only one user. The bill has a category i.e.
 * Groceries, Clothing, etc...
 * 
 *
 */
public class Bill implements Discountable {
    
    private User user;
    
    // the bill's net before discounts are applied
    private BigDecimal net;
    
    // the bill's net after the discounts are applied
    private BigDecimal netPayable;
    
    private CategoryType category;
    
    // discounts that will always be applied
    private List<Discount> alwaysApplicableDiscounts;
    
    // discounts that are mutually exclusive, if one is applied,
    // the rest won't
    private List<Discount> mutuallyExclusiveDiscounts;
        
    /**
     * Create a new bill for a user with net and category
     * @param user that the bill belongs to
     * @param net the net total of the bill
     * @param category the category of the bill
     */
    public Bill(User user, BigDecimal net, CategoryType category) {
        super();
        
        if(net == null) {
            throw new IllegalArgumentException("net is required");
        }
        
        if(user == null) {
            throw new IllegalArgumentException("user is require");
        }
        
        this.user = user;
        this.net = net;
        this.category = category;
    }


    /**
     * Apply the discounts attached to this instance and return
     * the net after the discounts have been applied
     * @return the net after the discounts are applied
     */
    public BigDecimal applyDiscounts() {
        
        if(this.net == null) {
            throw new IllegalArgumentException("net is required");
        }
        
        // start of with netPayable equals to net
        netPayable = net;
        
        BigDecimal discountAmount = null;
        
        // apply the mutually exclusive discounts first, they are applied in the order they
        // are inserted into the ArrayList. We could extend this approach to explicitly set the order
        // in the discount based on an instance variable
        if((mutuallyExclusiveDiscounts != null) && !mutuallyExclusiveDiscounts.isEmpty()) {
            
            for(Discount discount: mutuallyExclusiveDiscounts) {
                
                discountAmount = discount.calculate(this);
                
                if(discountAmount != null) {
                    // one discount was applied now exit
                    break;
                }
            }
            
            // if any discount was applied then take it off the netPayable
            if(discountAmount != null) {
                netPayable = netPayable.subtract(discountAmount);
            }
        }

        // apply the always applicable
        if((alwaysApplicableDiscounts != null) && !alwaysApplicableDiscounts.isEmpty()) {
            
            for(Discount discount: alwaysApplicableDiscounts) {
                
                discountAmount = discount.calculate(this);
                
                if(discountAmount != null) {
                    // apply it
                    netPayable = netPayable.subtract(discountAmount);
                }
            }
        }
        
        return netPayable;
    }
    
    @Override
    public BigDecimal getNetPayable() {
        return netPayable;
    }


    /**
     * @param netPayable the netPayable to set
     */
    public void setNetPayable(BigDecimal netPayable) {
        this.netPayable = netPayable;
    }


    @Override
    public User getUser() {
        return user;
    }
    
    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }


    /**
     * @return the net
     */
    public BigDecimal getNet() {
        return net;
    }

    /**
     * @param net the net to set
     */
    public void setNet(BigDecimal net) {
        this.net = net;
    }

    /**
     * @return the category
     */
    public CategoryType getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(CategoryType category) {
        this.category = category;
    }

    /**
     * @return the alwaysApplicable
     */
    public List<Discount> getAlwaysApplicable() {
        return alwaysApplicableDiscounts;
    }

    /**
     * @param alwaysApplicable the alwaysApplicable to set
     */
    public void setAlwaysApplicable(List<Discount> alwaysApplicable) {
        this.alwaysApplicableDiscounts = alwaysApplicable;
    }

    /**
     * @return the mutuallyExclusive
     */
    public List<Discount> getMutuallyExclusive() {
        return mutuallyExclusiveDiscounts;
    }

    /**
     * @param mutuallyExclusive the mutuallyExclusive to set
     */
    public void setMutuallyExclusive(List<Discount> mutuallyExclusive) {
        this.mutuallyExclusiveDiscounts = mutuallyExclusive;
    }

}
