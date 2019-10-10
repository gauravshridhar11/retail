package com.retail.model.user;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.retail.types.UserType;

public class UserTest {
    
    private Date today;
    private User user;

    @Before
    public void setUp() throws Exception {
        today = new Date();
        user = new User(today, UserType.EMPLOYEE);
    }

    // can't change user's internal state by modifying the date object
    @Test
    public void testUserDateUserType() {
        assertEquals(today, user.getCustomerSince());
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        today.setTime(calendar.getTime().getTime());
        
        assertFalse(today.equals(user.getCustomerSince()));
        
    }

    @Test
    public void testGetCustomerSince() {
        assertEquals(today, user.getCustomerSince());
        assertNotSame(today, user.getCustomerSince());
    }
    
    @Test
    public void testNullDate() {
        User user = new User(null, UserType.AFFILIATE);
        
        assertNull(user.getCustomerSince());
    }

}
