package edu.uci.ics.issm.jenkins;

public class ExternalPackage extends MyFile
{
	private String packageName;
	
	public ExternalPackage(String fileName, String packageName)
	{
		super(fileName);
		this.packageName = packageName;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	public int compareTo(ExternalPackage that)
	{
		return this.packageName.compareTo(that.getPackageName());
	}
	public String toString()
	{
		return "Package Name: " + this.packageName + "\n" +
					 "File Name   : " + this.fileName;
	}
}
