package edu.uci.ics.issm.jenkins;
import java.util.ArrayList;

import edu.uci.ics.issm.svn.ExternalPackage;

public class ExternalPackageNode implements Comparable
{
	private ExternalPackage p;
	private ArrayList<ExternalPackageNode> deps;
	
	public ExternalPackageNode(ExternalPackage p)
	{
		this.p = p;
		this.deps = new ArrayList<ExternalPackageNode>();
	}
	public void addDep(ExternalPackageNode that)
	{
		this.deps.add(that);
	}
	public String toString()
	{
		return p.toString();
	}
	public int compareTo(ExternalPackageNode that)
	{
		return this.p.compareTo(that.p);
	}
	public int compareTo(String that)
	{
		return this.p.getPackageName().compareTo(that);
	}
	@Override
	public int compareTo(Object arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	public void setScript(String s)
	{
		this.p.setFileName(s);
	}
	public ArrayList<ExternalPackageNode> getDeps()
	{
		return this.deps;
	}
	public ExternalPackage getExternalPackage()
	{
		return this.p;
	}
}
