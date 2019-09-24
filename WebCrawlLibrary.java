package com.granicus.migration.generic;

import org.testng.annotations.Test;

import com.opencsv.CSVReader;

import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.annotations.Test;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

public class WebCrawlLibrary implements IAutoConstant {

	static {
		System.setProperty(CHROME_KEY, CHROME_VALUE);
	}

	public static String sites;
	public static String sheetSiteName;
	public static Xls_Reader reader;

	// Setting up the workbook
	@BeforeTest
	public static void setUp() throws IOException {

		String crawlDirectory = "./Reports";
		File directories = new File(crawlDirectory);
		if (!directories.exists()) {
			directories.mkdirs();
		}

		File file = new File("./Reports/WebCrawler.xlsx");
		if (!file.exists())
			file.createNewFile();
		reader = new Xls_Reader(file);

	}

	// Get Site's URLs from the CSV
	public static void getSiteURLS() throws IOException {
		String[] row;
		CSVReader preReader = new CSVReader(new FileReader(Sites_CSV_PATH));

		row = preReader.readNext();

		try {
			while (row != null) {

				row = preReader.readNext();

				if (row != null) {

					sites = row[0];
					System.out.println(sites);
					if (sites.length() <= 31) {
						sheetSiteName = sites;
						reader.addSheet(sheetSiteName);
						reader.addColumn(sheetSiteName, "URL");
						reader.addColumn(sheetSiteName, "ResponseCode");
						reader.addColumn(sheetSiteName, "ResponseMessage");
					} else {
						sheetSiteName = sites.substring(0, 31);
						reader.addSheet(sheetSiteName);
						reader.addColumn(sheetSiteName, "URL");
						reader.addColumn(sheetSiteName, "ResponseCode");
						reader.addColumn(sheetSiteName, "ResponseMessage");
					}

					String postURL = row[2];
					System.out.println(postURL);

					LinkCrawl.getPageLinks(postURL, 0);
					ImageCrawl.getImageLinks(postURL, 0);
					JavaScriptCrawl.getJSLinks(postURL, 0);
					CSSCrawl.getCSSLinks(postURL, 0);

				} else {
					System.out.println("No Further Data found in CSV");
				}
			}
		} catch (Exception e) {
			System.out.println("Error in CSV");
			e.printStackTrace();
		}
		try {
			preReader.close();
		} catch (Exception e) {
			System.out.println("Error while closing CSV Reader !!!");
			e.printStackTrace();
		}

	}
}
