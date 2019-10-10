package com.retail.model.discount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.retail.model.bill.Bill;
import com.retail.model.user.User;
import com.retail.types.CategoryType;
import com.retail.types.DiscountType;
import com.retail.types.UserType;

public class CustomerPeriodDiscountTest {
    
    private Bill bill;

    private User user;

    private CustomerPeriodDiscount discount;

    @Before
    public void setUp() throws Exception {
        
        // 3 years ago
        Date date = DateUtils.addYears(new Date(), -3);

        user = new User(date, UserType.EMPLOYEE);

        bill = new Bill(user, new BigDecimal(450), CategoryType.GROCERIES);

        Set<CategoryType> exclude = new HashSet<>();
        exclude.add(CategoryType.GROCERIES);

        discount = 
                new CustomerPeriodDiscount(DiscountType.PERCENTAGE, new BigDecimal(5), exclude, 24);
    }

    @Test
    public void testCustomerPeriodDiscountValid() {
        CustomerPeriodDiscount discount = 
                new CustomerPeriodDiscount(DiscountType.AMOUNT, new BigDecimal(5), null, 24);
        
        assertEquals(new Integer(24), discount.getMonths());
        assertEquals(DiscountType.AMOUNT, discount.getType());
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testCustomerPeriodDiscountInvalidDiscount() {
         new CustomerPeriodDiscount(DiscountType.PERCENTAGE, null, null, 24);
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testCustomerPeriodDiscountInvalidMonths() {
         new CustomerPeriodDiscount(DiscountType.PERCENTAGE, new BigDecimal(10), null, null);
    }

    @Test
    public void testIsApplicableInvalidDiscountable() {
        try {
            discount.isApplicable(null);
            fail("expected exception not thrown");
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        
        bill.setUser(null);
        try {
            discount.isApplicable(bill);
            fail("expected exception not thrown");
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        
        user.setCustomerSince(null);
        bill.setUser(user);
        
        try {
            discount.isApplicable(bill);
            fail("expected exception not thrown");
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIsApplicableInvalidDiscount() {
        discount.setMonths(null);
        discount.isApplicable(bill);
    }
    
    @Test
    public void testIsApplicable() {
        assertFalse(discount.isApplicable(bill));
        
        bill.setCategory(CategoryType.ELECTRONICS);
        assertTrue(discount.isApplicable(bill));
        
        bill.setCategory(null);
        assertTrue(discount.isApplicable(bill));
        
        bill.setCategory(CategoryType.CLOTHING);
        discount.setExclude(null);
        assertTrue(discount.isApplicable(bill));
        
        discount.setMonths(36);
        assertFalse(discount.isApplicable(bill));
        
        discount.setMonths(35);
        assertTrue(discount.isApplicable(bill));
        
    }
    
    // ---- Calculate tests
    
    @Test
    public void testCalculateInvalid() {
        try {
            discount.calculate(null);
            fail("expected exception not thrown");
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        
        
        // netPayable is null
        try {
            discount.calculate(bill);
            fail("expected exception not thrown");
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        
        discount.setDiscount(null);
        bill.setNetPayable(new BigDecimal(120));
        
        try {
            discount.calculate(bill);
            fail("expected exception not thrown");
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testCalculateNotApplicable() {
        bill.setNetPayable(new BigDecimal(220));
        // category excluded
        assertNull(discount.calculate(bill));

        // setting the customer period to 3 years
        discount.setMonths(36);
        assertNull(discount.calculate(bill));
    }
    
    @Test
    public void testCalculateApplicable() {
        bill.setNetPayable(bill.getNet());
        
        bill.setCategory(CategoryType.ELECTRONICS);
        
        // 5% off $450
        BigDecimal amount = discount.calculate(bill);
        assertNotNull(amount);
        
        assertEquals(new BigDecimal(22.50).setScale(2), amount);
        
        // 10% off $125.50
        bill.setNetPayable(new BigDecimal(125.50));
        discount.setDiscount(new BigDecimal(10));
        amount = discount.calculate(bill);
        assertNotNull(amount);
        
        assertEquals(new BigDecimal(12.55).setScale(2, RoundingMode.HALF_UP), amount);
        
        
        // discount with amount simply returns the amount
        discount.setType(DiscountType.AMOUNT);
        
        amount = discount.calculate(bill);
        
        assertEquals(new BigDecimal(10).setScale(2), amount);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateInvalidDiscountType() {
        bill.setNetPayable(bill.getNet());
        bill.setCategory(CategoryType.ELECTRONICS);
        discount.setType(null);
        
        discount.calculate(bill);
    }

}
