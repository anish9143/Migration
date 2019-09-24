package com.granicus.migration.generic;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.BeforeTest;

import com.opencsv.CSVReader;

public class VersionLibrary implements IAutoConstant {

	public static WebDriver driver;
	public static String sites;
	public static VersionPage version;

	
	// Setting up driver properties
	static {
		System.setProperty(CHROME_KEY, CHROME_VALUE);
	}

	
	// Setting up headless browser
	@BeforeTest

	public void testSetUp() {

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setHeadless(true);

		driver = new ChromeDriver(chromeOptions);

	}

	// Getting Site's URLs
	public static void getSiteURL() throws IOException {

		String[] row;
		CSVReader versionReader = new CSVReader(new FileReader(Version_CSV_PATH));

		row = versionReader.readNext();

		try {
			while (row != null) {

				row = versionReader.readNext();

				if (row != null) {

					sites = row[0];
					System.out.println(sites);

					String versionURL = row[1];
					System.out.println(versionURL);

					driver.get(versionURL);
					// creating reference of VersionPage Pom Page class and utilising it
					version = new VersionPage(driver);
					String structureVersion = version.getStructureVersion();
				} else {
					System.out.println("No Further Data found in CSV");
				}
			}
		} catch (Exception e) {
			System.out.println("Error in CSV");
			e.printStackTrace();
		}
		try {
			versionReader.close();
		} catch (Exception e) {
			System.out.println("Error while closing CSV Reader !!!");
			e.printStackTrace();
		}

	}

}
