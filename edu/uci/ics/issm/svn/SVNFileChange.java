package edu.uci.ics.issm.svn;

public class SVNFileChange extends SVNFile
{
	private char modType;
	public SVNFileChange(String filePath, String fileName, char modType)
	{
		super(filePath, fileName);
		
		this.modType = modType;
	}
	public char getModType()
	{
		return modType;
	}
	public void setModType(char modType)
	{
		this.modType = modType;
	}

}
