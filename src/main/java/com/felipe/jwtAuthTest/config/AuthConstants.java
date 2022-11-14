package com.felipe.jwtAuthTest.config;

import java.util.concurrent.TimeUnit;

public class AuthConstants {

    public static final String SECRET = "crazy_ass_secret";
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(15);
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = TimeUnit.DAYS.toMillis(15);
    public static final String TOKEN_PREFIX = "Bearer ";
}
