package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.persistence.dtos.user.PasswordChangeDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * Test password validations.
 *
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PasswordValidationTest {
    private static final String GOOD_PASS = "ToyCar11!";
    private static final String SHORT_PASS = "abcd";
    private static final String BAD_PASS = "welcome";


    @Test
    public void testValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        final PasswordChangeDto dto = new PasswordChangeDto();

        // Test Good Password
        dto.setCurrentPassword("nopass");
        dto.setPassword(GOOD_PASS);
        dto.setPassword1(GOOD_PASS);

        Set<ConstraintViolation<PasswordChangeDto>> violationSet = validator.validate(dto);
        Assert.assertEquals("Expected good password", 0, violationSet.size());

        // Test Bad Password
        dto.setPassword(BAD_PASS);
        dto.setPassword1(BAD_PASS);

        violationSet = validator.validate(dto);
        Assert.assertEquals("Expected bad password.", 2, violationSet.size());

        // Test Short Password
        dto.setPassword1(SHORT_PASS);
        dto.setPassword(SHORT_PASS);

        violationSet = validator.validate(dto);
        Assert.assertEquals("Expected bad password", 2, violationSet.size());

    }

}
