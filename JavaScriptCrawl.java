package com.granicus.migration.generic;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JavaScriptCrawl {

	private static final int MAX_DEPTH = 4;
	private static HashSet<String> links = new HashSet<String>();

	public static org.jsoup.Connection.Response response;

	public static void getJSLinks(String URL, int depth) {
		if ((!links.contains(URL) && (depth < MAX_DEPTH)) && !URL.isEmpty()) {

			try {
				links.add(URL);
				Document document = Jsoup.connect(URL).ignoreContentType(true).get();
				Elements linksOnPage = document.select("script[src]");
				URL url = new URL(URL);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				int statusCode = con.getResponseCode();
				String statusMessage = con.getResponseMessage();
				System.out.println(">> Depth: " + depth + "Contains URL:  " + links.contains(URL) + " [" + URL + "]"
						+ "--->" + statusCode + "--->" + statusMessage);
				int rowCount = WebCrawlLibrary.reader.getRowCount(WebCrawlLibrary.sheetSiteName);

				System.out.println("URL Set? "
						+ WebCrawlLibrary.reader.setCellData(WebCrawlLibrary.sheetSiteName, "URL", rowCount, URL));

				System.out.println(
						"RC Set ? " + WebCrawlLibrary.reader.setCellData(WebCrawlLibrary.sheetSiteName, "ResponseCode", rowCount, statusCode + ""));

				System.out.println(
						"RM Set ? " + WebCrawlLibrary.reader.setCellData(WebCrawlLibrary.sheetSiteName, "ResponseMessage", rowCount, statusMessage));

				depth++;
				for (Element page : linksOnPage) {
					getJSLinks(page.attr("abs:src"), depth);
				}
			} catch (IOException e) {
				System.out.println(URL + e.getMessage());
			}
		}
	}

}
