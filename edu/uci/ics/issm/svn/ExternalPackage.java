package edu.uci.ics.issm.svn;

public class ExternalPackage extends SVNFile
{
	private String packageName;

	public ExternalPackage(String fileName, String packageName)
	{
		// TODO: This is too specific a path. Will make portability weak.
		// Definitely need something better.
		super("/issm/trunk-jpl/externalpackages/" + packageName + "/", fileName);
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
		return "Package Name: " + this.packageName + "\n" + "File Name   : " + this.fileName;
	}
}
