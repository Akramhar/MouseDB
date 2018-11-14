package edu.umassmed.mousedb.core;

import java.sql.Date;
import java.time.LocalDate;

public class DateConverter {

	public static LocalDate from(final Date date) {
		return date == null ? null : date.toLocalDate();
	}

	public static Date to(final LocalDate ld) {
		return ld == null ? null : Date.valueOf(ld);
	}
}