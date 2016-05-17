package edu.uci.ics.issm.jenkins;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class ExternalPackageSet extends HashSet<ExternalPackageNode>
{

	/**
	 * I'm not even sure why the fuck this is required.
	 */
	private static final long serialVersionUID = 816434612643997315L;

	public ExternalPackageSet()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public ExternalPackageSet(Collection<? extends ExternalPackageNode> arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ExternalPackageSet(int arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ExternalPackageSet(int arg0, float arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public ExternalPackageNode find(String s)
	{
		for(Iterator<ExternalPackageNode> i = this.iterator(); i.hasNext();)
		{
			ExternalPackageNode tmp = i.next();
			if(tmp.compareTo(s) == 0)
				return tmp;
		}
		return null;
	}

	public LinkedList<ExternalPackage> genExternalPackageOrder()
	{
		LinkedList<ExternalPackage> list = new LinkedList<ExternalPackage>();

		for(Iterator<ExternalPackageNode> i = this.iterator(); i.hasNext();)
		{
			traversePostOrder(i.next(), list);
		}
		return list;
	}

	private void traversePostOrder(ExternalPackageNode epn, LinkedList<ExternalPackage> list)
	{
		for(Iterator<ExternalPackageNode> i = epn.getDeps().iterator(); i.hasNext();)
		{
			traversePostOrder(i.next(), list);
		}
		if(!list.contains(epn.getExternalPackage()))
			list.add(epn.getExternalPackage());
	}
	public HashSet<ExternalPackage> calculateDeps(ExternalPackage p)
	{
		HashSet<ExternalPackage> set = new HashSet<ExternalPackage>();
		
		for(Iterator<ExternalPackageNode> i = this.iterator(); i.hasNext();)
			traverseCalculateDeps(i.next(), p, set);
		
		return set;
	}
	private boolean traverseCalculateDeps(ExternalPackageNode epn, ExternalPackage p, HashSet<ExternalPackage> set)
	{
		if(epn.getExternalPackage().compareTo(p) == 0)
		{
			set.add(p);
			return true;
		}
		
		boolean dep = false;

		for(Iterator<ExternalPackageNode> i = epn.getDeps().iterator(); i.hasNext();)
		{
			if(traverseCalculateDeps(i.next(), p, set))
			{
				dep = true;
				break;
			}
		}
		if(dep)
			set.add(epn.getExternalPackage());
		
		return dep;
	}
}
