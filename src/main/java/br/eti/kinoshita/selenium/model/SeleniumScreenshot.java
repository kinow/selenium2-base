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
package br.eti.kinoshita.selenium.model;

import java.io.File;
import java.io.Serializable;

/**
 * <p>
 * A screen shot taken by Selenium. This bean is used to generate a TAP Stream
 * containing Base64 encoded images.
 * </p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @see <a href="http://www.testanything.org">Test Anything Protocol</a>
 * @since 0.1
 */
public class SeleniumScreenshot implements Serializable
{

	private static final long serialVersionUID = 2634058555572731340L;

	private File file;
	private String description;
	private String fileType;
	private String title;

	public SeleniumScreenshot()
	{
		super();
	}

	/**
	 * Constructor with parameters.
	 * 
	 * @param file
	 *            The screen shot original file.
	 * @param description
	 *            A description for the screen shot.
	 * @param fileType
	 *            MIME file-type.
	 * @param title
	 *            A title for the screen shot. Be creative!
	 */
	public SeleniumScreenshot(File file, String description, String fileType,
			String title)
	{
		super();

		this.file = file;
		this.description = description;
		this.fileType = fileType;
		this.title = title;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile( File file )
	{
		this.file = file;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public String getFileType()
	{
		return fileType;
	}

	public void setFileType( String fileType )
	{
		this.fileType = fileType;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle( String title )
	{
		this.title = title;
	}

}
