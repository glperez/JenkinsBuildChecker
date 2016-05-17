package edu.uci.ics.issm.jenkins;
public class MyFile implements Comparable<MyFile>
{
	protected String fileName;
	
	public MyFile(String fileName)
	{
		this.fileName = fileName;
	}
	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	public int compareTo(MyFile that)
	{
		return this.fileName.compareTo(that.fileName);
	}
}
