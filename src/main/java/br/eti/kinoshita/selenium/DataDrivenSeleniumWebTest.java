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
import java.util.Arrays;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;

import br.eti.kinoshita.selenium.util.SeleniumWebTestException;

/**
 * <p>An Excel data driven Selenium web test.</p>
 * 
 * <p>It behaves exactly as {@link SeleniumWebTest}, with the difference that 
 * it contains a TestNG {@link DataProvider} that is created from an Excel 
 * file. This Excel file name comes from selenium.properties.</p>
 * 
 * <p>This solution was found whilst navigating on the Internet. Check it 
 * out here: http://www.qualitytesting.info/forum/topics/how-to-do-data-driven-using. 
 * If you know whom is the original author, drop me a note please, and I will 
 * make sure to have her/his name here as author, or I will pay some 
 * Baden-Baden beers.</p>
 * 
 * <p>// TBD: think in a way to have Data Driven Tests from properties files, 
 * XML, JSON, YAML, or any other test data source. Not only from Excel.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @author Cesar Fernandes de Almeida
 * @since 0.1
 */
public abstract class DataDrivenSeleniumWebTest 
extends SeleniumWebTest
{

	// To be able to have different loggers for selenium web tests and ddt's.
	protected static final Logger LOGGER = LoggerFactory.getLogger( DataDrivenSeleniumWebTest.class );
	
	/**
	 * @return Name of Table in the XLS file. By default this value is "TABLE". 
	 * Override this method and return the constant you used in your Excel files 
	 * to delimit test data cells.
	 */
	public String getTableName()
	{
		return "Table";
	}
	
	/**
	 * @return Name of the Sheet in the XLS file.
	 */
	public abstract String getSheetName();
	
	/**
	 * This is the method that enables data-driven tests. It returns an multi-
	 * dimensional array of Objects to be used in tests. 
	 * 
	 * @returnan multidimensional array of Objects to be used in tests. 
	 * @throws SeleniumWebTestException
	 */
	@DataProvider(name = "DataExcel")
	public Object[][] getDataFromXLS() 
	throws SeleniumWebTestException 
	{
		Object[][] xlsDataArray = getTableArray(getSheetName(), getTableName());
	    LOGGER.debug("Data-driven test object[][] array: " + Arrays.toString(xlsDataArray));
		return xlsDataArray;
	}
	
	/**
	 * Reads a XLS File and return an array of an array of objects, 
	 * just like testng data providers
	 *
	 * @param sheetName Excel sheet name
	 * @param tableName Excel table name
	 * 
	 * @throws SeleniumWebTestException
	 */
	protected String[][] getTableArray(String sheetName, String tableName) 
	throws SeleniumWebTestException
	{
		String[][] tableArray = null;
	    String xlsFile = getConfiguration().getString("selenium.xls");
	    
	    LOGGER.debug("Opening excel file " + xlsFile);
	    
		String xlsFilePath = null;
	    
		try
		{
			xlsFilePath = ClassLoader.getSystemResource(xlsFile).getFile();
			
			Workbook workbook = Workbook.getWorkbook(new File( xlsFilePath ) );
			
			Sheet sheet = workbook.getSheet(sheetName); 
		    
		    int startRow, startCol, endRow, endCol, ci, cj;
		    
		    Cell tableStart = sheet.findCell(tableName);
		    startRow = tableStart.getRow();
		    startCol = tableStart.getColumn();

		    Cell tableEnd= sheet.findCell(tableName, startCol+1,startRow+1, 100, 64000,  false);                

		    endRow=tableEnd.getRow();
		    endCol=tableEnd.getColumn();
		    
		    LOGGER.debug("startRow="+startRow+", endRow="+endRow+", startCol="+startCol+", endCol="+endCol);
		    
		    tableArray = new String[endRow-startRow-1][endCol-startCol-1];
		    ci=0;

		    for (int i=startRow+1;i<endRow;i++,ci++)
		    {
		    	cj=0;
		        for (int j=startCol+1;j<endCol;j++,cj++)
		        {
		        	tableArray[ci][cj]=sheet.getCell(j,i).getContents();
		        }
		    }
		    
		    // TBD: verify if it's really ok to throw this exception here
		    if ( tableArray.length <= 0 )
		    {
		    	throw new SeleniumWebTestException("Empty excel data.");
		    }
		    
			return tableArray;
		} 
		catch ( IOException ioe )
		{
			LOGGER.error("IO Exception retrieving table array from Excel: " + ioe.getMessage(), ioe);
			throw new SeleniumWebTestException(ioe);
		}
		catch ( BiffException be )
		{
			LOGGER.error("Internal error retrieving table array from Excel: " + be.getMessage(), be);
			throw new SeleniumWebTestException(be);
		}
	}	
	
}
