package com.retail.model.discount;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.retail.model.bill.Bill;
import com.retail.model.user.User;
import com.retail.types.CategoryType;
import com.retail.types.DiscountType;
import com.retail.types.UserType;

/**
 * Test covering the UserTypeDiscount class
 *
 *
 */
public class UserTypeDiscountTest {
    
    private Bill bill;
    
    private User user;
    
    private UserTypeDiscount discount;

    @Before
    public void setUp() throws Exception {
        
        user = new User(new Date(), UserType.EMPLOYEE);
        
        bill = new Bill(user, new BigDecimal(450), CategoryType.GROCERIES);
        
        Set<CategoryType> exclude = new HashSet<>();
        exclude.add(CategoryType.GROCERIES);
        
        discount = 
                new UserTypeDiscount(DiscountType.PERCENTAGE, new BigDecimal(30), exclude, UserType.EMPLOYEE);
    }

    @Test
    public void testUserTypeDiscountValid() {
        UserTypeDiscount discount = 
                new UserTypeDiscount(DiscountType.AMOUNT, new BigDecimal(10), null, UserType.AFFILIATE);
        
        assertEquals(UserType.AFFILIATE, discount.getUserType());
        assertEquals(DiscountType.AMOUNT, discount.getType());
    }
    
    @Test
    public void testUserTypeDiscountDefaultType() {
        UserTypeDiscount discount = 
                new UserTypeDiscount(null, new BigDecimal(10), null, UserType.CUSTOMER);
        
        assertEquals(UserType.CUSTOMER, discount.getUserType());
        assertEquals(DiscountType.PERCENTAGE, discount.getType());
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testUserTypeDiscountInvalidDiscount() {
         new UserTypeDiscount(DiscountType.PERCENTAGE, null, null, UserType.AFFILIATE);
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testUserTypeDiscountInvalidUserType() {
         new UserTypeDiscount(DiscountType.PERCENTAGE, new BigDecimal(10), null, null);
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
        
        user.setType(null);
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
        discount.setUserType(null);
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
        
        discount.setUserType(UserType.AFFILIATE);
        assertFalse(discount.isApplicable(bill));
        
    }
    
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

        // non matching userType
        discount.setUserType(UserType.AFFILIATE);
        assertNull(discount.calculate(bill));
    }
    
    @Test
    public void testCalculateApplicable() {
        bill.setNetPayable(bill.getNet());
        
        bill.setCategory(CategoryType.ELECTRONICS);
        
        // 30% off $450
        BigDecimal amount = discount.calculate(bill);
        assertNotNull(amount);
        
        assertEquals(new BigDecimal(135).setScale(2), amount);
        
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
