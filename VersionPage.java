package com.granicus.migration.generic;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class VersionPage extends VersionLibrary {

	// Creating the repositories of elements to be used from the version page of the
	// site

	@FindBy(xpath = "//*[contains(text(),'Structure Version')]")

	WebElement structureVersion;

	@FindBy(xpath = "//*[contains(text(),'Data Version')]")

	WebElement dataVersion;

	public VersionPage(WebDriver driver) {

		PageFactory.initElements(driver, this);

	}

	// Utilising the initialised elements 
	public String getStructureVersion() {
		String text = structureVersion.getText();
		System.out.println(text);
		return text;
	}

	public void getDataVersion() {
		dataVersion.getText();
	}

}
