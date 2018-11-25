package br.com.e8.soupsi.client.util;

import java.util.Calendar;
import java.util.Date;

public class DataUtil {

	public static Date getDataAtual() {
		return new Date();
	}
	
	public static Date addMinutes(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		
		return cal.getTime();
	}
	
	public static int anoAtual() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDataAtual());
		int year = cal.get(Calendar.YEAR);
		
		return year;
	}
	
	public static int mesAtual() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDataAtual());
		int month = cal.get(Calendar.MONTH);
		
		return month;
	}
}