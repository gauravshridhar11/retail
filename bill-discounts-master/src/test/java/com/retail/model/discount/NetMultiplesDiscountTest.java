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


public class NetMultiplesDiscountTest {

    private Bill bill;

    private User user;

    private NetMultiplesDiscount discount;

    @Before
    public void setUp() throws Exception {

        // 3 years ago
        Date date = DateUtils.addYears(new Date(), -3);

        user = new User(date, UserType.EMPLOYEE);

        bill = new Bill(user, new BigDecimal(450), CategoryType.GROCERIES);

        Set<CategoryType> exclude = new HashSet<>();
        exclude.add(CategoryType.GROCERIES);

        discount = 
                new NetMultiplesDiscount(new BigDecimal(5), exclude, new BigDecimal(100));
    }

    @Test
    public void testNetMultiplesDiscountValid() {
        NetMultiplesDiscount discount = 
                new NetMultiplesDiscount(new BigDecimal(5), null, new BigDecimal(100));

        assertEquals(new BigDecimal(100), discount.getNetMultiples());
        assertEquals(DiscountType.AMOUNT, discount.getType());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNetMultiplesDiscountInvalidDiscount() {
        new NetMultiplesDiscount(null, null, new BigDecimal(100));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNetMultiplesDiscountNullNetMultiples() {
        new NetMultiplesDiscount(new BigDecimal(5), null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNetMultiplesDiscountZeroNetMultiples() {
        new NetMultiplesDiscount(new BigDecimal(5), null, BigDecimal.ZERO);
    }


    @Test
    public void testIsApplicableInvalidDiscountable() {
        try {
            discount.isApplicable(null);
            fail("expected exception not thrown");
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }

        bill.setNetPayable(null);
       
        try {
            discount.isApplicable(bill);
            fail("expected exception not thrown");
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsApplicableInvalidDiscount() {
        bill.setNetPayable(new BigDecimal(99));
        discount.setNetMultiples(null);
        discount.isApplicable(bill);
    }
    
    @Test
    public void testIsApplicable() {
        bill.setCategory(CategoryType.CLOTHING);
        bill.setNetPayable(new BigDecimal(99));
        assertFalse(discount.isApplicable(bill));

        bill.setNetPayable(new BigDecimal(100));
        assertTrue(discount.isApplicable(bill));
        
        bill.setNetPayable(new BigDecimal(210));
        discount.setNetMultiples(new BigDecimal(200));
        assertTrue(discount.isApplicable(bill));

        bill.setCategory(null);
        assertTrue(discount.isApplicable(bill));

        discount.setExclude(null);
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
        discount.setNetMultiples(new BigDecimal(300));
        assertNull(discount.calculate(bill));
    }

    @Test
    public void testCalculateApplicable() {
        bill.setNetPayable(bill.getNet());

        bill.setCategory(CategoryType.ELECTRONICS);

        // $5 off for every $100 from a net of $450
        BigDecimal amount = discount.calculate(bill);
        assertNotNull(amount);

        assertEquals(new BigDecimal(20.00).setScale(2), amount);

        // $10 off for every $200 from a net of $990
        bill.setNetPayable(new BigDecimal(990));
        discount.setNetMultiples(new BigDecimal(200));
        discount.setDiscount(new BigDecimal(10));
        amount = discount.calculate(bill);
        assertNotNull(amount);

        assertEquals(new BigDecimal(40.00).setScale(2, RoundingMode.HALF_UP), amount);

    }


}
