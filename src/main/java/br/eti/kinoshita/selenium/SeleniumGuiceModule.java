package br.eti.kinoshita.selenium;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Guice Module for dependency injection in TestNG tests.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class SeleniumGuiceModule 
implements Module
{

	protected final static Logger LOGGER = LoggerFactory.getLogger( SeleniumGuiceModule.class );
	
	/**
	 * Selenium Configuration instance.
	 */
	protected static final Configuration CONFIGURATION = new CompositeConfiguration();
	
	private static final WebDriver DRIVER;
	
	/*
	 * Static constructor.
	 */
	static
	{
		try
		{
			PropertiesConfiguration propertiesConfig = 
					new PropertiesConfiguration("selenium.properties");
			((CompositeConfiguration)CONFIGURATION).addConfiguration(propertiesConfig);
			((CompositeConfiguration)CONFIGURATION).addConfiguration(new SystemConfiguration());
			((CompositeConfiguration)CONFIGURATION).setThrowExceptionOnMissing(Boolean.TRUE);
		}
		catch( ConfigurationException ce )
		{
			LOGGER.error("Failed to load selenium.properties: " + ce.getMessage(), ce);
		}
		String browser = CONFIGURATION.getString("selenium.browser");
		
		if ( ! StringUtils.isBlank(browser) )
		{
			if ( browser.equals("firefox") )
			{
				DRIVER = new FirefoxDriver();
			}
			else if ( browser.equals("html") )
			{
				DRIVER = new HtmlUnitDriver();
			}
			else if ( browser.equals("chrome") )
			{
				DRIVER = new ChromeDriver();
			}
			else
			{
				DRIVER = new InternetExplorerDriver();
			}
		}
		else 
		{
			DRIVER = new InternetExplorerDriver();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.google.inject.Module#configure(com.google.inject.Binder)
	 */
	public void configure( Binder binder )
	{
		binder.bind(WebDriver.class).toInstance(DRIVER);
		binder.bind(Configuration.class).toInstance(CONFIGURATION);
	}

}
