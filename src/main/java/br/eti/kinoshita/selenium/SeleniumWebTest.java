/* 
 * The MIT License
 * 
 * Copyright (c) 2011 Bruno P. Kinoshita <http://www.kinoshita.eti.br>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.eti.kinoshita.selenium;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.tap4j.ext.testng.TAPAttribute;
import org.tap4j.ext.testng.TestTAPReporter;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Listeners;

import br.eti.kinoshita.selenium.model.SeleniumScreenshot;
import br.eti.kinoshita.selenium.util.SeleniumConfig;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
@Listeners(value=TestTAPReporter.class)
public abstract class SeleniumWebTest 
{
	
	/**
	 * The logger.
	 */
	protected final static Logger LOG = Logger.getLogger( SeleniumWebTest.class );
	
	/**
	 * The WebDriver instance used throught our tests.
	 */
	protected static WebDriver driver = null;
	
	/**
	 * Selenium Configuration instance.
	 */
	protected static SeleniumConfig config = SeleniumConfig.getInstance();

	/*
	 * Static constructor.
	 */
	static
	{
		String browser = config.getProperty("selenium.browser");
		
		if ( StringUtils.isBlank( browser ) || browser.equals("firefox") )
		{
			FirefoxProfile profile = new FirefoxProfile();
			profile.setEnableNativeEvents( false );
			driver = new FirefoxDriver( profile );
		} 
		else if ( browser.equals("ie") )
		{
			driver = new InternetExplorerDriver();
		}
		else if( browser.equals("chrome") )
		{
			driver = new ChromeDriver();
		}
		else
		{
			LOG.fatal("Invalid driver: " + browser);
			throw new RuntimeException( "Invalid driver: " + browser );
		}
	}
	
	/**
	 * Closes the driver and quit. This method is annotated to always run.
	 */
	@AfterTest(alwaysRun=true)
	public void tearDown()
	{
		try
		{
			driver.close();
			driver.quit();
		}
		catch ( Throwable t )
		{
			LOG.debug( t );
		}
	}
	
	/**
	 * Adds a screen shot to the list of attributes.
	 * 
	 * @param context TestNG test context.
	 * @param method TestNG test method.
	 * @param description Screen shot description.
	 */
	public void addScreenShot( ITestContext context, Method method, String description )
	{
		if ( driver instanceof TakesScreenshot )
		{
			LOG.debug("Taking screenshot with driver " + driver.getTitle());
			File attachment = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			SeleniumScreenshot screenshot = new SeleniumScreenshot(attachment, description, "image/png", attachment.getName());
			this.addScreenShot(context, method, screenshot);
		}
		else
		{
			LOG.warn("Driver " + driver.getTitle() + " does not support taking screenshots. Use a different one please.");
		}
	}

	@SuppressWarnings("unchecked")
	private void addScreenShot( ITestContext context, Method method, SeleniumScreenshot screenshot )
	{
		Object o = context.getAttribute("Files");
		Map<String, Object> filesMap = null;
		if ( o == null )
		{
			filesMap = new LinkedHashMap<String, Object>();
		}
		else 
		{
			TAPAttribute attr = (TAPAttribute)o;
			if ( attr.getMethod() != method )
			{
				filesMap = new LinkedHashMap<String, Object>();
			}
			else
			{
				filesMap = (Map<String, Object>) attr.getValue();
			}
		}
		
		Map<String, Object> fileMap = new LinkedHashMap<String, Object>();
		
		File file = screenshot.getFile();
		
		fileMap.put("File-Location", file.getAbsolutePath() );
		fileMap.put("File-Title", screenshot.getTitle() );
		fileMap.put("File-Description", screenshot.getDescription() );
		fileMap.put("File-Size", file.length() );
		fileMap.put("File-Name", file.getName());
		
		byte[] fileData = null;
		try
		{
			fileData = FileUtils.readFileToByteArray(file);
		} 
		catch (IOException e)
		{
			Assert.fail("Failed to read file to byte array.", e);
		}
		String content = Base64.encodeBase64String( fileData );
		
		fileMap.put("File-Content", content);
		fileMap.put("File-Type", screenshot.getFileType());
		
		filesMap.put(file.getAbsolutePath(), fileMap);
		
		TAPAttribute attribute = new TAPAttribute(method, filesMap);
		context.setAttribute("Files", attribute);
	}
	
}
