package com.itservice.creditservice.constant;

import java.util.function.Function;

import com.itservice.creditservice.util.EnumUtils;

	public enum HeaderName {


			LOANID("Loan ID"),
			CUSTID("Customer ID"),
			LOANAMT("Current Loan Amount"),
			TERM("Term"),
			CREDITSCORE("Credit Score"),
			ANNUALINC("Annual Income"),
			YRSINJOB("Years in current job"),
			HOMEOWERSHIP("Home Ownership"),
			PURPOSE("Purpose"),
			MONDEBT("Monthly Debt"),
			YRSOFCRET("Years of Credit History"),
			MONOFLASTDEL("Months since last delinquent"),
			NUMOPENACT("Number of Open Accounts"),
			NUMCREDPROB("Number of Credit Problems"),
			CURCRETBAL("Current Credit Balance"),
			MAXOPENCRET("Maximum Open Credit"),
			BANKRUPT("Bankruptcies"),
			TAXLINE("Tax Liens");



			private String description; 
			
			private static final Function<String, HeaderName> func = EnumUtils.lookupMap(HeaderName.class, e -> e.getDesp());

			private HeaderName(String description) { 
				this.description = description;
			}
			
			public String getDesp() {
				return description;
			}

			/**
			 * Retrieves the Enum object of the given description.
			 * 
			 * @param description The description of the Enum.
			 * @return The Enum object. Otherwise, {@literal null}.
			 */
			public static HeaderName lookupByDescription(String description) {
				return func.apply(description);
			}
	}
