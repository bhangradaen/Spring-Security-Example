package com.util;

import com.exception.InvalidExpirationTimeException;

import java.util.Date;

public class DateTimeUtil {

    public static Date generateExpirationDateForTimeInMilliseconds(long expirationTimeInMilliseconds) {
        Date now = new Date();
        boolean providedExpirationTimeIsInvalid = expirationTimeInMilliseconds < 0;
        if (providedExpirationTimeIsInvalid)
            throw new InvalidExpirationTimeException("Expiration time can not be negative.");
        return new Date(
                now.getTime() + expirationTimeInMilliseconds);
    }

}
