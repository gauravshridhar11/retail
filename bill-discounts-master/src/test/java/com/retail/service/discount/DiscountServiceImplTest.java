/**
 * The contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2014, Ecarf.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.retail.service.discount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.retail.model.discount.CustomerPeriodDiscount;
import com.retail.model.discount.Discount;
import com.retail.model.discount.GenericDiscount;
import com.retail.model.discount.NetMultiplesDiscount;
import com.retail.model.discount.UserTypeDiscount;
import com.retail.types.CategoryType;
import com.retail.types.DiscountType;
import com.retail.types.UserType;

public class DiscountServiceImplTest {
    
    private DiscountService service;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        
        service = new DiscountServiceImpl();
    }

    /**
     * Test method for {@link com.retail.service.discount.DiscountServiceImpl#loadAlwayApplicableDiscounts()}.
     */
    @Test
    public void testLoadAlwayApplicableDiscounts() {
        
        List<Discount> discounts = service.loadAlwayApplicableDiscounts();
        
        assertNotNull(discounts);
        
        assertEquals(1, discounts.size());
        
        NetMultiplesDiscount discount = (NetMultiplesDiscount) discounts.get(0);
        
        validateDiscount(discount, new BigDecimal(5), DiscountType.AMOUNT, true);
        
        assertEquals(new BigDecimal(100), discount.getNetMultiples());
    }

    /**
     * Test method for {@link com.retail.service.discount.DiscountServiceImpl#loadMutuallyExclusiveDiscounts()}.
     */
    @Test
    public void testLoadMutuallyExclusiveDiscounts() {
        List<Discount> discounts = service.loadMutuallyExclusiveDiscounts();

        assertNotNull(discounts);

        assertEquals(3, discounts.size());
        
        // check the order of the discounts
        // employee discount
        UserTypeDiscount discount = (UserTypeDiscount) discounts.get(0);
        
        validateDiscount(discount, new BigDecimal(30), DiscountType.PERCENTAGE, false);
                
        assertEquals(UserType.EMPLOYEE, discount.getUserType());
        
        Set<CategoryType> exclude = discount.getExclude();        
        validateExclude(exclude, CategoryType.GROCERIES);
        
        // affiliate discount
        discount = (UserTypeDiscount) discounts.get(1);
        
        validateDiscount(discount, new BigDecimal(10), DiscountType.PERCENTAGE, false);
                
        assertEquals(UserType.AFFILIATE, discount.getUserType());
        
        exclude = discount.getExclude();        
        validateExclude(exclude, CategoryType.GROCERIES);
        
        
        // user period
        CustomerPeriodDiscount periodDiscount = (CustomerPeriodDiscount) discounts.get(2);
        
        validateDiscount(periodDiscount, new BigDecimal(5), DiscountType.PERCENTAGE, false);
                
        assertEquals(new Integer(24), periodDiscount.getMonths());
        
        exclude = periodDiscount.getExclude();        
        validateExclude(exclude, CategoryType.GROCERIES);
    }
    
    private void validateDiscount(GenericDiscount discount, BigDecimal amount, 
            DiscountType type, boolean emptyExclude) {
        
        assertNotNull(discount);
        
        assertEquals(amount, discount.getDiscount());
        
        if(emptyExclude) {
            assertNull(discount.getExclude());
        } else {
            assertNotNull(discount.getExclude());
        }
        
        assertEquals(type, discount.getType());
    }
    
    private void validateExclude(Set<CategoryType> exclude, CategoryType... categories) {
        assertNotNull(exclude);
        assertEquals(categories.length, exclude.size());
        
        for(CategoryType category: categories) {
            assertTrue(exclude.contains(category));
        }
    }

}
