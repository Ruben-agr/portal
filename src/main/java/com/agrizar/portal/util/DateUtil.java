package com.agrizar.portal.util;

import java.time.LocalDateTime;
import java.util.Date;

public final class DateUtil {

	public static Date getDate() {
		return new Date();
	}
	
	public static LocalDateTime getLocalDateTime() {
		return LocalDateTime.now();
	}
}
