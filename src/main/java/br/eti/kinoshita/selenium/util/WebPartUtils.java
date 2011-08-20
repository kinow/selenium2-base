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
package br.eti.kinoshita.selenium.util;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Select;


/**
 * TBD: use slf4j
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
final public class WebPartUtils
{
	private static final Logger LOG = Logger.getLogger(WebPartUtils.class);
	
	private WebPartUtils() {}
	
	/**
	 * Wait for assync content in a determined period
	 * 
	 * @param driver
	 * @param by
	 * @param timeout
	 * @return
	 * @throws NoSuchElementException
	 */
	public static WebElement waitForAssyncContent( WebDriver driver, By by, Long timeout ) 
	throws NoSuchElementException
	{
		long end = System.currentTimeMillis() + (timeout);
		WebElement renderedWebElement = null;
		
		while ( System.currentTimeMillis() < end )
		{
			try
			{
				renderedWebElement = driver.findElement( by );
			}
			catch ( NoSuchElementException nsee )
			{
				LOG.debug(nsee);
			}
			
			if ( renderedWebElement != null && renderedWebElement.isEnabled() && renderedWebElement.isDisplayed() )
			{
				return renderedWebElement;
			}
			
			try
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException ie)
			{
				LOG.debug(ie);
			}
		}
		
		if ( renderedWebElement == null )
		{
			throw new NoSuchElementException( "Could not locate assync content" );
		}
		
		try
		{
			if ( renderedWebElement.isDisplayed() )
			{
				throw new NoSuchElementException( "Element is not being displayed" );
			}
		} 
		catch (Throwable t)
		{
			LOG.debug(t);
			t.printStackTrace( System.err );
		}
		
		return renderedWebElement;
	}
	
	/**
	 * Wait for determined select index in a select (combo) box in a determined period
	 * 
	 * @param select
	 * @param index
	 * @param timeout
	 */
	public static void waitForSelectIndex( Select select, Integer index, Long timeout )
	{
		Long end = System.currentTimeMillis() + timeout;
		
		while( System.currentTimeMillis() < end )
		{
			if ( select.getOptions().size() < index )
			{
				try
				{
					Thread.sleep( 1000 );
				} 
				catch (InterruptedException e)
				{
					LOG.debug(e);
				}
			} 
			else 
			{
				break;
			}
		}
	}
	
	/**
	 * Execute a javascript command
	 * 
	 * @param driver
	 * @param jsCommand
	 */
	public static void javascriptExec(WebDriver driver, String jsCommand)
	{
		((JavascriptExecutor)driver).executeScript( jsCommand );	
	}
	
	
	/**
	 * Execute the javascript onclick event
	 * 
	 * @param driver
	 * @param elementID
	 */
	public static void javascriptClickEvent(WebDriver driver, String elementID)
	{
		String jsCommand = "var el = document.getElementById('"+elementID+"'); var evt; if ( document.createEvent ) { evt = document.createEvent('MouseEvents'); evt.initMouseEvent('click', true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null); if ( el != null ) { el.dispatchEvent( evt ); } } else if ( el != null && el.fireEvent ) { el.fireEvent('onclick'); }";
		((JavascriptExecutor)driver).executeScript( jsCommand );	
	}
	
	/**
	 * Click on an element (with browser treatment)
	 * 
	 * @param driver
	 * @param buttonId
	 * @throws Exception
	 */
	public static void elementClick(WebDriver driver, String buttonId, Long timeout)
	throws SeleniumWebTestException 
	{
		WebElement tButton = waitForAssyncContent(driver, By.id(buttonId), timeout);
		
		if(tButton==null)
		{
			throw new SeleniumWebTestException("Button with ID ="+buttonId+" not found in the document.");
		}
		
		if(driver instanceof InternetExplorerDriver)
		{
			tButton.sendKeys(Keys.ENTER);
		}
		else if(driver instanceof FirefoxDriver)
		{
			javascriptExec(driver, "var el = document.getElementById('"+buttonId+"'); el.focus();");
			tButton.click();
		}
		else
		{
			throw new SeleniumWebTestException("Driver for this browser has not been tested.");
		}
	}	
	
	
	/**
	 * Switch a WebDriver from one webElement to another one by a given Xpath 
	 * 
	 * @param webDriver
	 * @param xpath
	 * @param timeout
	 * @return
	 */
	public static WebDriver switchToWebElementByXpath(WebDriver webDriver, String xpath, Long timeout)
	{
		WebElement iframeElement = waitForAssyncContent(webDriver, By.xpath(xpath), timeout ); 
		return webDriver.switchTo().frame( iframeElement );
	}

}
