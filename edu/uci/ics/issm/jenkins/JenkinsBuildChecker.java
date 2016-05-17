package edu.uci.ics.issm.jenkins;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JenkinsBuildChecker
{

	public static void main(String[] args) throws IOException
	{
		/*
		 * TODO Process options 1) File with list of externalpackages scripts. 2)
		 * Revision number range. 3) Potentially other options such as whether
		 * output should be to console or to a single file or multiple files.
		 */

		/*
		 * TODO Output configuration
		 */
		DAVRepositoryFactory.setup();
		String url = "https://issm.ess.uci.edu/svn/issm/issm/trunk-jpl";
		String name = "glperez";
		String password = "*uEOe#f2UYeh5pOs";
		long startRevision = 0;
		long endRevision = -1; // HEAD (the latest) revision
		long latestRevision = -1;
		boolean recompile = false;
		
		SVNRepository repository = null;
		Collection<SVNLogEntry> logEntries = null;
		ArrayList<SVNFile> changeList = new ArrayList<SVNFile>();

		File lastRevision = new File("./resources/lastRevision");
		
		if(lastRevision.exists() && !lastRevision.isDirectory())
		{
			System.out.println("Grabbing old revision number");
			
			Scanner sc = new Scanner(lastRevision);
			startRevision = Long.parseLong(sc.nextLine());
			sc.close();
		}
		System.out.println("Old rev: " + startRevision );
		try
		{
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
			repository.setAuthenticationManager(authManager);
			
			latestRevision = (int) repository.getLatestRevision();

			logEntries = repository.log(new String[]
			{ "" }, null, startRevision, endRevision, true, true);
		}
		catch(Exception e)
		{
			System.err.println("Caught the following exception: " + e);
			e.printStackTrace();
		}

		for(Iterator<SVNLogEntry> entries = logEntries.iterator(); entries.hasNext();)
		{
			SVNLogEntry logEntry = entries.next();

			if(logEntry.getChangedPaths().size() > 0)
			{
				Set<String> changedPathsSet = logEntry.getChangedPaths().keySet();

				for(Iterator<String> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();)
				{
					SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());

					String fullFilePath = entryPath.getPath();
					int pos = fullFilePath.lastIndexOf('/');

					String fileName = fullFilePath.substring(pos + 1);
					String filePath = fullFilePath.substring(0, pos + 1);

					changeList.add(new SVNFile(filePath, fileName, entryPath.getType()));
				}
			}
		}
		System.out.println("--------------------Change List--------------------");
		for(Iterator<SVNFile> i = changeList.iterator(); i.hasNext();)
		{
			System.out.println(i.next());
		}
		System.out.println("--------------------Change List--------------------");

		
		ExternalPackageSet externalPackageSet = new ExternalPackageSet();

		try
		{
			File inputFile = new File("./resources/input.txt");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("ExternalPackage");
			
			for(int temp = 0; temp < nList.getLength(); temp++)
			{
				Node nNode = nList.item(temp);
				if(nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) nNode;
					
					ExternalPackageNode curNode = externalPackageSet.find(eElement.getAttribute("name"));
					
					if(curNode == null)
					{
						curNode = new ExternalPackageNode(new ExternalPackage(
							eElement.getElementsByTagName("script").item(0).getTextContent(), eElement.getAttribute("name")));
						externalPackageSet.add(curNode);
					}
					else
					{
						curNode.setScript(eElement.getElementsByTagName("script").item(0).getTextContent());
					}
					
					NodeList depList = eElement.getElementsByTagName("dependency");
					for(int i = 0; i < depList.getLength(); i++)
					{
						ExternalPackageNode depNode = externalPackageSet.find(depList.item(i).getTextContent());
						
						if(depNode == null)
						{
							depNode = new ExternalPackageNode(new ExternalPackage("no script", depList.item(i).getTextContent()));
							externalPackageSet.add(depNode);
						}
						curNode.addDep(depNode);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		LinkedList<ExternalPackage> externalPackagelist = externalPackageSet.genExternalPackageOrder();
		HashSet<ExternalPackage> externalPackageRecompileSet = new HashSet<ExternalPackage>();
		
		for(Iterator<ExternalPackage> i = externalPackagelist.iterator(); i.hasNext();)
		{
			ExternalPackage ep = i.next();
			
			if(checkExternalPackage(changeList, ep))
			{
				externalPackageRecompileSet.addAll(externalPackageSet.calculateDeps(ep));
				recompile = true;
			}
			
			//System.out.println(ep.getPackageName() + " : " + ep.getFileName());
		}
		System.out.println("--------------------Checking Which Externalpackages Need Recompiling--------------------");
		for(Iterator<ExternalPackage> i = externalPackagelist.iterator(); i.hasNext();)
		{
			ExternalPackage ep = i.next();
			
			if(externalPackageRecompileSet.contains(ep))
				System.out.println(ep);
		}
		System.out.println("--------------------Checking Which Externalpackages Need Recompiling--------------------");

		
		System.out.println("--------------------Checking if Recompilation is Needed--------------------");
		if(recompile || checkRecompile(changeList))
			System.out.println("We need to recompile!");
		else
			System.out.println("We do not need to recompile.");
		System.out.println("--------------------Checking if Recompilation is Needed--------------------");

		//ExternalPackage tmp = new ExternalPackage("no script", "autotools");
		//HashSet<ExternalPackage> tmpSet = externalPackageSet.calculateDeps(tmp);
		
		
		PrintStream ps = new PrintStream(new FileOutputStream(lastRevision));
		
		System.out.println("Current Revision Number: " + latestRevision);
		ps.println(latestRevision);
		ps.close();
	}

	public static boolean checkRecompile(ArrayList<SVNFile> changeList)
	{
		Pattern srcCodeExt = Pattern.compile(".*\\.(cpp)|(f)|(c)");

		for(Iterator<SVNFile> i = changeList.iterator(); i.hasNext();)
		{
			Matcher m = srcCodeExt.matcher(i.next().getFileName());

			if(m.matches())
				return true;
		}
		return false;
	}

	public static boolean checkExternalPackage(ArrayList<SVNFile> changeList, ExternalPackage p)
	{
		Pattern  notaScript = Pattern.compile(".*\\/externalpackages\\/" + p.getPackageName() + "\\/" + ".*(?<!\\.sh)");
		
		for(Iterator<SVNFile> i = changeList.iterator(); i.hasNext();)
		{
			SVNFile f = i.next();
			Matcher m = notaScript.matcher(f.filePath + f.fileName);
						
			if(f.fileName.compareTo(p.fileName) == 0 || m.matches())
				return true;
		}
		return false;
	}
}
