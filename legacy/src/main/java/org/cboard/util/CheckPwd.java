package org.cboard.util;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

public final class CheckPwd {

	public static ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
}
