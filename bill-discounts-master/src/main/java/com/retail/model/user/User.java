package com.retail.model.user;

import java.util.Date;

import com.retail.types.UserType;

/**
 * Represents a retail website user, users can be employees, affiliates or
 * customers.
 * 
 * 
 *
 */
public class User {
    
    private Date customerSince;
    
    private UserType type;
      
    /**
     * Create a new user
     * @param customerSince the date the user is using the retail website
     * @param type the user type, e.g. employee, affiliate or customer
     */
    public User(Date customerSince, UserType type) {
        super();
        if(customerSince != null) {
            this.customerSince = new Date(customerSince.getTime());
        }
        this.type = type;
    }

    /**
     * @return the customerSince
     */
    public Date getCustomerSince() {
        Date since = this.customerSince;
        if(this.customerSince != null) {
            since = new Date(customerSince.getTime());
        }
        return since;
    }

    /**
     * @return the type
     */
    public UserType getType() {
        return type;
    }

    /**
     * @param customerSince the customerSince to set
     */
    public void setCustomerSince(Date customerSince) {
        
        if(customerSince != null) {
            this.customerSince = new Date(customerSince.getTime());
        } else {
            this.customerSince = null;
        }
    }

    /**
     * @param type the type to set
     */
    public void setType(UserType type) {
        this.type = type;
    }
    
}
