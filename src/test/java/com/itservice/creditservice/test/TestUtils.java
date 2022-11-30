package com.itservice.creditservice.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.time.Instant;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.itservice.creditservice.collection.Credit;
import com.itservice.creditservice.constant.HeaderName;
import com.itservice.creditservice.repository.CreditRepository;
import com.itservice.creditservice.util.MyUtils;

public class TestUtils {

	final static String authorizationHeader = "Basic " + DatatypeConverter.printBase64Binary("rbcfolks:password".getBytes());

	private static MultipartFile createCsv() {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final CSVFormat format = CSVFormat.DEFAULT;
		final CSVPrinter csvPrinter;
		MultipartFile result = null;
		try {
			csvPrinter = new CSVPrinter(new PrintWriter(out), format);
			csvPrinter.printRecord(
					"Loan ID,Customer ID,Current Loan Amount,Term,Credit Score,Annual Income,Years in current job,Home Ownership,Purpose,Monthly Debt,Years of Credit History,Months since last delinquent,Number of Open Accounts,Number of Credit Problems,Current Credit Balance,Maximum Open Credit,Bankruptcies,Tax Liens");
			csvPrinter.printRecord(
					"f738779f-c726-40dc-92cf-689d73af533d,ded0b3c3-6bf4-4091-8726-47039f2c1b90,611314,Short Term,747,2074116,10+ years,Home Mortgage,Debt Consolidation,42000.83,21.8,NA,9,0,621908,1058970,0,0");
			csvPrinter.printRecord(
					"6dcc0947-164d-476c-a1de-3ae7283dde0a,1630e6e3-34e3-461a-8fda-09297d3140c8,266662,Short Term,734,1919190,10+ years,Home Mortgage,Debt Consolidation,36624.4,19.4,NA,11,0,679573,904442,0,0");
			csvPrinter.flush();
			csvPrinter.close();

			System.out.println(csvPrinter.toString());

			String name = "file.csv";
			String originalFileName = "file.csv";
			String contentType = "text/csv";

			result = new MockMultipartFile(name, originalFileName, contentType, out.toByteArray());

			// Read the file
			Reader reader = new InputStreamReader(result.getInputStream());

			// Create CSVParser
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

			// Loop the records
			csvParser.forEach(c -> {
				System.out.println(c);
			});

			// Close CSVParser
			csvParser.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
