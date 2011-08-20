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

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import br.eti.kinoshita.selenium.model.SeleniumPropertiesBean;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public final class SeleniumConfig 
implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3271120985401763209L;

	/**
	 * Default properties file: selenium.properties.
	 */
	private static final String DEFAULT_PROPERTIES_FILE = "selenium.properties";
	

	private static final Logger LOG = Logger.getLogger(SeleniumConfig.class);
	
	
	private SeleniumPropertiesBean seleniumPropertiesForm;
	

	private final Properties p = new Properties();
	
	/**
	 * Default constructor.
	 */
	private SeleniumConfig()
	{
		super();
		
		this.seleniumPropertiesForm = new SeleniumPropertiesBean();
		
		String propertiesFile = System.getProperty("selenium.properties");
		if ( StringUtils.isEmpty( propertiesFile ) )
		{
			propertiesFile = DEFAULT_PROPERTIES_FILE;
		}
		
		try
		{
			p.load(ClassLoader.getSystemResourceAsStream(propertiesFile));
		} 
		catch (IOException e)
		{
			LOG.error("Error loading properties file: " + e.getMessage(), e);
		}
		
		String sUrl = p.getProperty("selenium.url");
		if ( ! StringUtils.isEmpty(sUrl) )
		{
			LOG.debug( "Tests URL: " + sUrl );
			this.seleniumPropertiesForm.setUrl( sUrl );
		}
		
		String sXls = p.getProperty("selenium.xls");
		if ( ! StringUtils.isEmpty(sXls) )
		{
			LOG.debug( "Tests XLS: " + sXls );
			this.seleniumPropertiesForm.setXls( sXls );
		}
		
		String sTimeout = p.getProperty("selenium.timeout");
		if( ! StringUtils.isEmpty(sTimeout) )
		{
			LOG.debug( "Tests Time-Out: " + sTimeout );
			this.seleniumPropertiesForm.setTimeout( Long.parseLong(sTimeout) );
		}
		
		String sBrowser = p.getProperty("selenium.browser");
		if ( ! StringUtils.isEmpty(sBrowser) )
		{
			LOG.debug( "Tests Browser: " + sBrowser );
			this.seleniumPropertiesForm.setBrowser(sBrowser);
		}
	}
	
	private final static SeleniumConfig _INSTANCE = new SeleniumConfig();
	
	public static SeleniumConfig getInstance()
	{
		return _INSTANCE;
	}


	public String getProperty(String key)
	{
		return this.p.getProperty(key);
	}
	
	public void setProperty(String key, String value)
	{
		this.p.setProperty(key, value);
	}
	
	
	/**
	 * @return the seleniumPropertiesForm
	 */
	public SeleniumPropertiesBean getSeleniumPropertiesForm() {
		return seleniumPropertiesForm;
	}

	/**
	 * @param seleniumPropertiesForm the seleniumPropertiesForm to set
	 */
	public void setSeleniumPropertiesForm(
			SeleniumPropertiesBean seleniumPropertiesForm) {
		this.seleniumPropertiesForm = seleniumPropertiesForm;
	}
	
	/**
	 * @return the SeleniumPropertiesForm url
	 */
	public String getUrl() {
		return this.getSeleniumPropertiesForm().getUrl();
	}
	/**
	 * @return the SeleniumPropertiesForm xls
	 */
	public String getXls() {
		return this.getSeleniumPropertiesForm().getXls();
	}
	/**
	 * @return the SeleniumPropertiesForm timeout
	 */
	public Long getTimeout() {
		return this.getSeleniumPropertiesForm().getTimeout();
	}
	/**
	 * @return the SeleniumPropertiesForm browser
	 */
	public String getBrowser() {
		return this.getSeleniumPropertiesForm().getBrowser();
	}
	
	
	/**
	 * Prints the configuration into the given PrintStream
	 * 
	 * @param ps 
	 * 		PrintSteam
	 */
	public void printTo( PrintStream ps )
	{
		// ps.println();
		ps.println( "# Selenium configuration #" );
		ps.println( "# URL: " + this.seleniumPropertiesForm.getUrl() );
		ps.println( "# XLS: " + this.seleniumPropertiesForm.getXls() );
		ps.println( "# Time-Out: " + this.seleniumPropertiesForm.getTimeout() );
		ps.println( "# Browser: " + this.seleniumPropertiesForm.getBrowser() );
		// ps.println();
	}
	
}
