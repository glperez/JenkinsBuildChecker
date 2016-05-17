package edu.uci.ics.issm.svn;

public class SVNFile
{
	protected String fileName;
	protected String filePath;
	
	public SVNFile(String filePath, String fileName)
	{
		this.fileName = fileName;
		this.filePath = filePath;
	}
	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String path)
	{
		this.filePath = path;
	}

	public String toString()
	{
		return this.filePath + this.getFileName();
	}
	public int compareTo(SVNFile that)
	{
		int compareThisToThat = this.fileName.compareTo(that.fileName);
		
		if(compareThisToThat != 0)
			return compareThisToThat;
			
		return this.filePath.compareTo(that.filePath);
	}
}
