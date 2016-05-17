package edu.uci.ics.issm.jenkins;

public class SVNFile extends MyFile
{
	protected String filePath;
	protected char modType;
	
	public SVNFile(String filePath, String fileName, char modType)
	{
		super(fileName);
		this.filePath = filePath;
		this.modType = modType;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String path)
	{
		this.filePath = path;
	}

	public char getModType()
	{
		return modType;
	}

	public void setModType(char modType)
	{
		this.modType = modType;
	}
	public String toString()
	{
		return this.modType + " " + this.filePath + this.getFileName();
	}
	public int compareTo(SVNFile that)
	{
		int compareThisToThat = this.fileName.compareTo(that.fileName);
		
		if(compareThisToThat != 0)
			return compareThisToThat;
			
		return this.filePath.compareTo(that.filePath);
	}
}
