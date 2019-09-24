package com.granicus.migration.generic;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.frontendtest.components.ImageComparison;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.opencsv.CSVReader;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class ImageLibrary extends BaseTest implements IAutoConstant {

	public static String imagePath;
	public static String sites;
	public static String preScreenshotPath;
	public static String postScreenshotPath;

	/* Get Images From Respective Pre and Post Migration URLs and browsers */
	public static void getImages(String browser) throws IOException {
		String[] row;

		CSVReader csvReader = new CSVReader(new FileReader(Sites_CSV_PATH));

		row = csvReader.readNext();

		try {
			while (row != null) {

				row = csvReader.readNext();

				if (row != null) {

					sites = row[0];
					String preUrl = row[1];
					System.out.println(preUrl);
					driver.get(preUrl);

					preScreenshotPath = takeScreenshot(driver, browser + "_" + sites, browser, "Pre");

					sites = row[0];
					String postUrl = row[2];
					System.out.println(postUrl);

					driver.get(postUrl);
					postScreenshotPath = takeScreenshot(driver, browser + "_" + sites, browser, "Post");
					compareScreenshots(browser);
				} else {
					System.out.println("No Further Data found in CSV");
				}
			}
		} catch (Exception e) {
			System.out.println("Error in CSV");
			e.printStackTrace();
		}
		try {
			csvReader.close();
		} catch (Exception e) {
			System.out.println("Error while closing CSV Reader !!!");
			e.printStackTrace();
		}
	}

	/* Picking images and comparing Images */

	public static void compareScreenshots(String browser) throws IOException {

		reporter = new ExtentHtmlReporter("./Reports/" + sites + "/" + sites + "_" + browser + "_" + "Report.html");

		extent = new com.aventstack.extentreports.ExtentReports();
		extent.attachReporter(reporter);
		test = extent.createTest(sites);
		test.log(Status.INFO, "Welcome to Automated Report ! Please see the  ScreenShots for status  !!!");

		String imgOriginal = preScreenshotPath;
		String imgToCompareWithOriginal = postScreenshotPath;

		System.out.println(imgOriginal);
		System.out.println(imgToCompareWithOriginal);
		screenCapture("PreMigration Image", "./Screenshots" + "/" + browser + "/" + "Pre" + "/" + sites + ".png");
		test.addScreenCaptureFromPath("./Screenshots" + "/" + browser + "/" + "Pre" + "/" + sites + ".png");

		screenCapture("PostMigration Image", "./Screenshots" + "/" + browser + "/" + "Post" + "/" + sites + ".png"); //
		test.addScreenCaptureFromPath("./Screenshots" + "/" + browser + "/" + "Post" + "/" + sites + ".png");

		String imgOutputDifferences = "./Reports/" + sites + "/Screenshots/" + browser + "/" + "Result.png";

		ImageComparison imageComparison = new ImageComparison(40, 40, 0.05);

		if (imageComparison.fuzzyEqual(imgOriginal, imgToCompareWithOriginal, imgOutputDifferences)) {

			System.out.println("Images are fuzzy-equal.");

			int rowCount = reader.getRowCount(browser);
			System.out.println("Row Count " + rowCount);
			reader.setCellData(browser, "Site Name", rowCount, sites);
			reader.setCellData(browser, "Status", rowCount, "Images are Equal");

			screenCapture("Compared Image", "./Screenshots/" + browser + "/" + "Result.png");
			test.addScreenCaptureFromPath("./Screenshots/" + browser + "/" + "Result.png");

			test.log(Status.INFO, "Images are Same !");
		} else {
			System.out.println("Images are not fuzzy-equal.");
			int rowCount = reader.getRowCount(browser);
			reader.setCellData(browser, "Site Name", rowCount, sites);
			reader.setCellData(browser, "Status", rowCount, "Images are Not Equal");
			test.fail("Compared Image", MediaEntityBuilder
					.createScreenCaptureFromPath("./Screenshots/" + browser + "/" + "Result.png").build());
			test.addScreenCaptureFromPath("./Screenshots/" + browser + "/" + "Result.png");
			test.log(Status.INFO, "Images are Not Same !");

		}

		extent.flush();

	}

	/* \\ Capturing Full Page Screen Shots */
	public static String takeScreenshot(WebDriver driver, String screenshotName, String browser, String type)
			throws IOException, InterruptedException

	{
		Thread.sleep(2000);
		String imageDir = "./Reports/" + sites + "/Screenshots/" + browser + "/" + type;
		File directories = new File(imageDir);
		if (!directories.exists()) {
			directories.mkdirs();
		}

		imagePath = imageDir + "/" + sites + ".png";
		Screenshot fpScreenshot;
		if (browser.equalsIgnoreCase("edge")) {
			fpScreenshot = new AShot()
					.shootingStrategy(ShootingStrategies.viewportPasting(ShootingStrategies.scaling(1.0f), 3000))
					.takeScreenshot(driver);
			ImageIO.write(fpScreenshot.getImage(), "PNG", new File(imagePath));
		}

		else if (browser.equalsIgnoreCase("IE")) {
			fpScreenshot = new AShot().takeScreenshot(driver);
			ImageIO.write(fpScreenshot.getImage(), "PNG", new File(imagePath));
		}

		else {
			fpScreenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
			ImageIO.write(fpScreenshot.getImage(), "PNG", new File(imagePath));
		}

		return imagePath;

	}

	// Attaching Captured ScreenShot to Report

	public static Object screenCapture(String logDetails, String SSPath) throws IOException {

		test.log(Status.INFO, logDetails, MediaEntityBuilder.createScreenCaptureFromPath(SSPath).build());
		return test;
	}

}
