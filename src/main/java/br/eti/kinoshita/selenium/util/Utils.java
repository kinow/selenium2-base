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

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;


/**
 * <p>Utility methods. Most of these methods were found on Internet 
 * and implemented here while Selenium latest version was 2.0b3. Probably 
 * some of them might be deprecated or will be in some near future. Hopefully 
 * Selenium API will grow and become much nicer, making most if not all, of 
 * these methods useless.</p>
 * 
 * <p>// TBD: Review which methods could be replaced/removed/enhanced.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class Utils
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
	
	private Utils() {} // Hidden constructor as 'tis a utility class.
	
	public static Function<WebDriver, WebElement> presenceOfElement(
			final By locator )
	{
		return new Function<WebDriver, WebElement>()
		{
			public WebElement apply( WebDriver driver )
			{
				return driver.findElement(locator);
			}
		};
	}
	
	public static Function<WebDriver, Select> presenceOfSelectIndexAvailable(
			final By locator, final int index )
	{
		return new Function<WebDriver, Select>()
		{
			public Select apply( WebDriver driver )
			{
				WebElement foundElement = driver.findElement(locator);
				if (foundElement != null)
				{
					Select select = new Select( foundElement );
					if (select.getOptions().size() >= index)
					{
						return select;
					}
				}
				// TBD: or throw NoSuchException...
				return null;
			}
		};
	}
	
	/**
	 * Execute a javascript command
	 * 
	 * @param driver
	 * @param jsCommand
	 */
	public static void executeJavascript( WebDriver driver, String jsCommand )
	{
		((JavascriptExecutor) driver).executeScript(jsCommand);
	}
	
	/**
	 * Wait for assync content in a determined period
	 * 
	 * @param driver Selenium web driver.
	 * @param by Selenium By expression.
	 * @param timeout Selenium time out.
	 * @return a WebElement asynchronously loaded.
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
				LOGGER.debug(nsee.getMessage(), nsee);
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
				LOGGER.debug(ie.getMessage(), ie);
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
			LOGGER.debug(t.getMessage(), t);
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
					LOGGER.debug(e.getMessage(), e);
				}
			} 
			else 
			{
				break;
			}
		}
	}
	
	/**
	 * Execute the javascript onclick event
	 * 
	 * @param driver
	 * @param elementID
	 */
	public static void executeJavascriptClick( WebDriver driver,
			String elementID )
	{
		String jsCommand = "var el = document.getElementById('"
				+ elementID
				+ "'); var evt; if ( document.createEvent ) { evt = document.createEvent('MouseEvents'); evt.initMouseEvent('click', true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null); if ( el != null ) { el.dispatchEvent( evt ); } } else if ( el != null && el.fireEvent ) { el.fireEvent('onclick'); }";
		((JavascriptExecutor) driver).executeScript(jsCommand);
	}

	public static void executeJavascriptClickIE( WebDriver driver, String elementID )
	{
		String jsCommand = "var el = document.getElementById('"
			+ elementID
			+ "'); el.click();";
		((JavascriptExecutor) driver).executeScript(jsCommand);
	}
	
	/**
	 * Make actual thread sleep for X mili-seconds
	 * 
	 * @param timeToSleep
	 */
	public static void sleep(Long timeToSleep)
	{
		try 
		{
			Thread.sleep(timeToSleep);
		} 
		catch (InterruptedException e)
		{
			throw new SeleniumWebTestException(e);
		}
	}
	
	/**
	 * Click on a WebElement as many times are necessary checking 
	 * if some attribute value has changed
	 * 
	 * @param elementToClick
	 * @param attributeType
	 * @param attributeValue
	 */
	public static void clickAndWaitForElementAttributeChange(WebElement elementToClick, String attributeType, String attributeValue)
	{
		for( int i = 0 ; i < 10 ; ++i )
		{
			elementToClick.click();
			String elementAttribute = elementToClick.getAttribute(attributeType);
			
			if ( StringUtils.isNotBlank(elementAttribute) && elementAttribute.contains(attributeValue) )
			{
				sleep(750L);
			}
			else
			{
				break;
			}
		}
	}
	
	/**
	 * Click on some WebElement as many times are necessary checking 
	 * if it desappeared
	 * 
	 * @param elementToClick
	 */
	public static void clickAndWaitForElementToDesappear(WebElement elementToClick)
	{
		for( int i = 0 ; i < 10 ; ++i )
		{
			try 
			{
				elementToClick.click();
				sleep(750L);
			} 
			catch (ElementNotVisibleException e) 
			{
				break;
			}
		}
	}
	
	/**
	 * Select radio button
	 * 
	 * @param radioToClick
	 */
	public static void selectRadioButton(WebElement radioToClick)
	{
		for( int i = 0 ; i < 10 ; ++i )
		{
			radioToClick.click();
			
			if(radioToClick.isSelected())
			{
				break;
			}
		}
	}
	
}
