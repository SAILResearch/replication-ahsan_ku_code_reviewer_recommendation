package com.sail.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtil {

	
	public static DateTimeFormatter formatterWithHyphen = DateTimeFormat.forPattern("yyyy-MM-dd");
	public static DateTimeFormatter gitHubDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	public static DateTimeFormatter gitHubDateFormatterWithZone = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZZ");


	//public static DateTime studyDate = DateUtil.formatterWithHyphen.parseDateTime("2020-06-26");
	public static DateTime studyDate = DateUtil.formatterWithHyphen.parseDateTime("2022-01-01");


}
