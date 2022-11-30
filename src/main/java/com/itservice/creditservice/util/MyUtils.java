package com.itservice.creditservice.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import com.itservice.creditservice.collection.Credit;
import com.itservice.creditservice.constant.HeaderName;

public class MyUtils {

	public static Date convertDateTime(String dateStr, String timeStr) throws ParseException {

		String dtTime = dateStr + " " + timeStr;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
		Date parsedDt = new Date(sdf.parse(dtTime).getTime());
		return parsedDt;
	}

	public static String convertDateTimeInStr(String dateStr, String timeStr) throws ParseException {

		return dateStr + " " + timeStr;

	}

	public static Integer numStrToIntg(String s) {

		if (StringUtils.isNotBlank(s)) {
			return Integer.valueOf(s);
		} else {
			return 0;
		}
	}

	public static Double numStrToDoub(String s) {

		if (StringUtils.isNotBlank(s)) {
			return Double.valueOf(s);
		} else {
			return 0.0;
		}
	}

	public static Float numStrToFlt(String s) {

		if (StringUtils.isNotBlank(s)) {
			return Float.valueOf(s);
		} else {
			return Float.valueOf(0);
		}
	}

	public static Credit parseCsvRecord(CSVRecord c) {
		return  Credit.builder().loanId(c.get(HeaderName.LOANID.getDesp()))
				.customerId(c.get(HeaderName.CUSTID.getDesp()))
				.currentLoanAmt(MyUtils.numStrToDoub(c.get(HeaderName.LOANAMT.getDesp())))
				.term(c.get(HeaderName.TERM.getDesp()))
				.creditScore(MyUtils.numStrToIntg(c.get(HeaderName.CREDITSCORE.getDesp())))
				.annualIncome(MyUtils.numStrToDoub(c.get(HeaderName.ANNUALINC.getDesp())))
				.yearsInJob(c.get(HeaderName.YRSINJOB.getDesp()))
				.homeOwnership(c.get(HeaderName.HOMEOWERSHIP.getDesp())).purpose(c.get(HeaderName.PURPOSE.getDesp()))
				.monthlyDebt(MyUtils.numStrToDoub(c.get(HeaderName.MONDEBT.getDesp())))
				.yearsOfCredit(MyUtils.numStrToDoub(c.get(HeaderName.YRSOFCRET.getDesp())))
				.currentCreditBal(MyUtils.numStrToDoub(c.get(HeaderName.CURCRETBAL.getDesp())))
				.maxOpenCredit(MyUtils.numStrToDoub(c.get(HeaderName.MAXOPENCRET.getDesp()))).build();
	}

}
