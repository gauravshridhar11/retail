package com.retail.model.bill;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.retail.model.discount.Discount;
import com.retail.model.user.User;
import com.retail.service.discount.DiscountService;
import com.retail.service.discount.DiscountServiceImpl;
import com.retail.types.CategoryType;
import com.retail.types.UserType;

/**
 * Test the discounts applied to the Bill object. 
 * 
 * The discounts tested here are the one created by the DiscountsService
 * 
 *
 */
public class BillTest {

    private DiscountService service;
    
    private Bill bill;

    private User user;
    
    @Before
    public void setUp() throws Exception {
        
        service = new DiscountServiceImpl();
        
        // 3 years ago
        Date date = DateUtils.addYears(new Date(), -3);

        user = new User(date, UserType.EMPLOYEE);

        bill = new Bill(user, new BigDecimal(450), CategoryType.GROCERIES);
          
        // load the discounts and set them on the bill
        bill.setAlwaysApplicable(service.loadAlwayApplicableDiscounts());
        bill.setMutuallyExclusive(service.loadMutuallyExclusiveDiscounts());

    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBillInvalidNet() {
        new Bill(user, null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBillInvalidUser() {
        new Bill(null, new BigDecimal(450), null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testApplyDiscountsInvalidNet() {
        bill.setNet(null);
        bill.applyDiscounts();
    }

    /**
     * test apply discounts when no discounts are added
     */
    @Test
    public void testApplyDiscountsNoDiscount() {
        
        bill.setAlwaysApplicable(null);
        bill.setMutuallyExclusive(null);
        
        // bill total is 99.99
        BigDecimal net = new BigDecimal(99.99);
        bill.setNet(net);
        
        // no discounts should be applied
        BigDecimal netPayable = bill.applyDiscounts();
        
        assertEquals(net, netPayable);

        bill.setAlwaysApplicable(new ArrayList<Discount>());
        bill.setMutuallyExclusive(new ArrayList<Discount>());

        netPayable = bill.applyDiscounts();

        assertEquals(net, netPayable);


    }

    /**
     * Non of the discounts will be applicable
     */
    @Test
    public void testApplyDiscountsNonApplicable() {
        // customer for less that 2 years
        user.setType(UserType.CUSTOMER);
        user.setCustomerSince(DateUtils.addYears(new Date(), -1));
        
        // bill total is 99.99
        BigDecimal net = new BigDecimal(99.99);
        bill.setCategory(CategoryType.CLOTHING);
        bill.setNet(net);
        
        // no discounts should be applied
        BigDecimal netPayable = bill.applyDiscounts();
        
        assertEquals(net, netPayable);
        
    }
    
    /**
     * Bill has no groceries, User is an employee, with 3 years as a customer and have a bill of $1450
     * should get 30% discount as an employee - discount = $435, discounted net = $1450 - $435 = $1015
     * should get $5 off for every $100 - discount (off $1015) is $50, so final payable is $965
     */
    @Test
    public void testApplyDiscountsEmployee() {
        bill.setCategory(CategoryType.CLOTHING);
        bill.setNet(new BigDecimal(1450.00));
        
        BigDecimal netPayable = bill.applyDiscounts();
        assertEquals((new BigDecimal(965.00)).setScale(2), netPayable);
    }
    
    /**
     * Same scenario as testApplyDiscountsApplicable(), but bill is of groceries, 
     * should get $5 off for every $100 - discount (off $1450) is $70, so final payable is $1380
     */
    @Test
    public void testApplyDiscountsEmployeeWithGroceries() {
        bill.setNet(new BigDecimal(1450.00));
        
        BigDecimal netPayable = bill.applyDiscounts();
        assertEquals((new BigDecimal(1380.00)).setScale(2), netPayable);
    }
    
    
    /**
     * Bill has no groceries, User is an affiliate, with 3 years as a customer and have a bill of $1450
     * should get 10% discount as an affiliate - discount = $145, discounted net = $1450 - $145 = $1305
     * should get $5 off for every $100 - discount (off $1305) is $65, so final payable is $1240
     */
    @Test
    public void testApplyDiscountsAffiliate() {
        user.setType(UserType.AFFILIATE);
        bill.setCategory(CategoryType.CLOTHING);
        bill.setNet(new BigDecimal(1450.00));
        
        BigDecimal netPayable = bill.applyDiscounts();
        assertEquals((new BigDecimal(1240.00)).setScale(2), netPayable);
    }
    
    /**
     * Same scenario as testApplyDiscountsAffiliate(), but bill is of groceries, 
     * should get $5 off for every $100 - discount (off $1450) is $70, so final payable is $1380
     */
    @Test
    public void testApplyDiscountsAffiliateWithGroceries() {
        user.setType(UserType.AFFILIATE);
        bill.setNet(new BigDecimal(1450.00));
        
        BigDecimal netPayable = bill.applyDiscounts();
        assertEquals((new BigDecimal(1380.00)).setScale(2), netPayable);
    }
    
    /**
     * Bill has no groceries, User is a customer, with 3 years as a customer and have a bill of $1450
     * should get 5% discount as an affiliate - discount = $72.5, discounted net = $1450 - $72.5 = $1377.5
     * should get $5 off for every $100 - discount (off $1377.5) is $65, so final payable is $1312.5
     */
    @Test
    public void testApplyDiscountsCustomer() {
        user.setType(UserType.CUSTOMER);
        bill.setCategory(CategoryType.CLOTHING);
        bill.setNet(new BigDecimal(1450.00));
        
        BigDecimal netPayable = bill.applyDiscounts();
        assertEquals((new BigDecimal(1312.50)).setScale(2), netPayable);
    }
    
    /**
     * Same scenario as testApplyDiscountsAffiliate(), but bill is of groceries, 
     * should get $5 off for every $100 - discount (off $1450) is $70, so final payable is $1380
     */
    @Test
    public void testApplyDiscountsCustomerWithGroceries() {
        user.setType(UserType.CUSTOMER);
        bill.setNet(new BigDecimal(1450.00));
        
        BigDecimal netPayable = bill.applyDiscounts();
        assertEquals((new BigDecimal(1380.00)).setScale(2), netPayable);
    }
    
    /**
     * New customer joined a year ago, only getting the $5 off for every $100
     */
    @Test
    public void testApplyDiscountsNewCustomer() {
        user.setCustomerSince(DateUtils.addYears(new Date(), -1));
        user.setType(UserType.CUSTOMER);
        bill.setCategory(CategoryType.CLOTHING);
        bill.setNet(new BigDecimal(1450.00));
        
        BigDecimal netPayable = bill.applyDiscounts();
        assertEquals((new BigDecimal(1380.00)).setScale(2), netPayable);
    }
    

}
