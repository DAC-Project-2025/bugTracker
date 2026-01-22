package com.user_service.constants;

import java.util.concurrent.TimeUnit;

public final  class  SecurityConstants {
	 private SecurityConstants() {}

	    public static final String JWT_HEADER = "Authorization";
	    public static final String TOKEN_PREFIX = "Bearer ";

	    public static final long ACCESS_TOKEN_VALIDITY =
	            TimeUnit.HOURS.toMillis(24);

	    public static final long REFRESH_TOKEN_VALIDITY =
	            TimeUnit.DAYS.toMillis(7);
}
