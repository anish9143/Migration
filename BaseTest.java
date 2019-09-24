package com.granicus.migration.generic;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class BaseTest implements IAutoConstant {

	public static WebDriver driver;
	public static ExtentHtmlReporter reporter;
	public static com.aventstack.extentreports.ExtentReports extent;
	public static com.aventstack.extentreports.ExtentTest test;
	
	public static Xls_Reader reader;

	// Setting the driver properties
	static {
		System.setProperty(GECKO_KEY, GECKO_VALUE);
		System.setProperty(CHROME_KEY, CHROME_VALUE);
		System.setProperty(IE_KEY, IE_VALUE);
		System.setProperty(EDGE_KEY, EDGE_VALUE);
	}

	// Launching the Application

	@BeforeClass
	@Parameters("browser")
	public void openApplication(String browser) throws IOException {
		
		// Initialise consolidated Workbook for ImageComparison Result
		
		
		String imageDir = "./Reports";
		File directories = new File(imageDir);
		if (!directories.exists()) {
			directories.mkdirs();
		}

		File file = new File("./Reports/ImageComparisonStatus_"+browser+".xlsx");
		if (!file.exists())
			file.createNewFile();
		reader = new Xls_Reader(file);
		reader.addSheet(browser);
		
		
// Invoke respective webdrivers for respective browsers
		
		if (browser.equalsIgnoreCase("chrome")) {
			driver = new ChromeDriver();
			
			//Adding Sheet and column Name for the respective browser
			//reader.addSheet(browser);
			reader.addColumn(browser, "Site Name");
			reader.addColumn(browser, "Status");

		}

		else if (browser.equalsIgnoreCase("firefox")) {
			
			// Adding Sheet and column Name for the respective browser
			driver = new FirefoxDriver();
			
			reader.addColumn(browser, "Site Name");
			reader.addColumn(browser, "Status");

		} else if (browser.equalsIgnoreCase("IE")) {

			driver = new InternetExplorerDriver();
			//Adding Sheet and column Name for the respective browser
			//reader.addSheet(browser);
			reader.addColumn(browser, "Site Name");
			reader.addColumn(browser, "Status");

		} else {

			driver = new EdgeDriver();
			//Adding Sheet and column Name for the respective browser
			//reader.addSheet(browser);
			reader.addColumn(browser, "Site Name");
			reader.addColumn(browser, "Status");
		}
		driver.manage().window().maximize();

	}

	@AfterSuite
	public void tearDown() {

		driver.quit();
	}

}
