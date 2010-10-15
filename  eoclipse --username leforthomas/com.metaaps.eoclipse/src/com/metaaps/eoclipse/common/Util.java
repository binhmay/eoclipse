/*******************************************************************************
 * Copyright (c) 2010 METAAPS SRL(U).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     METAAPS SRL(U) - created by Thomas Lefort - initial API and implementation
 ******************************************************************************/
package com.metaaps.eoclipse.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.services.IServiceLocator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.metaaps.eoclipse.common.datasets.IDataContent;

/**
 * @author leforthomas
 * 
 * a set of convenience static methods
 * 
 */
public class Util {

	public static Object searchForInterface(Class clazz, Object[] objects){
		if(objects == null)
		{
			return null;
		}
		for(int i = 0; i < objects.length; i++)
		{
			if(clazz.isInstance(objects[i]))
			{
				return objects[i];
			}
		}
		return null;
	}
	
	public static Object scanTree(Class clazz, Object item)
	{
		if(item instanceof Model)
		{
			if(clazz.isInstance(item))
			{
				return item;
			} else {
				if(((Model)item).getParent() != null)
				{
					return scanTree(clazz, ((Model)item).getParent());
				} else {
					return null;
				}
			}
		}
		
		return null;
	}
	
	public static Object scanTreeChidren(Class clazz, Object item)
	{
		if(item instanceof Model)
		{
			if(clazz.isInstance(item))
			{
				return item;
			} else {
				Object[] chidren = ((Model)item).getChildren();
				if(chidren != null)
				{
					for(Object obj : chidren) {
						Object searcheditem = scanTreeChidren(clazz, obj);
						if(searcheditem != null) {
							return searcheditem;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public static Object getExtensionPointImplementation(String extensionpointid, String extensionpointname) {
		IExtensionPoint extensionpoint = Platform.getExtensionRegistry().getExtensionPoint(extensionpointid);
		IExtension[] extensions = extensionpoint.getExtensions();
		for (IExtension e : extensions) {
			IConfigurationElement[] elements = e.getConfigurationElements();
			for(IConfigurationElement element : elements)
			{
				if(element.getName().contentEquals(extensionpointname))
				{
					try {
						return element.createExecutableExtension("Class");
					} catch (CoreException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		
		return null;
	}
	
	public static Object scanTreePath(ITreeSelection selection, Class clazz) {
        TreePath[] treepaths = selection.getPaths();
        TreePath path = treepaths[0];
        for(int i = 0; i < path.getSegmentCount(); i++) {
        	Object obj = path.getSegment(i);
        	if(clazz.isInstance(obj)) {
        		return obj;
        	}
        }
        
        return null;
	}
	
	public static Object getAttributeFromExtension(String extensionid, String pointname, String attributename) {
		IExtensionPoint processing = Platform.getExtensionRegistry().getExtensionPoint(extensionid);
		IExtension[] extensions = processing.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements =
	            extension.getConfigurationElements();
			for(IConfigurationElement element : elements)
			{
				if(element.getName().contentEquals(pointname))
				{
					return element.getAttribute(attributename);
				}
			}
		}
		return null;
	}
	
	public static void addPopupMenu(String popupmenuid, String pluginid, String menulabel, String menuid, String commandid, Class triggerClazz, Map parameters) {
		IMenuService service = (IMenuService) PlatformUI.getWorkbench().getService(IMenuService.class);
		final Map fparameters = parameters;
	    final String fmenulabel = menulabel;
		final String fcommandid = commandid;
		final String fmenuid = menuid;
		final Class ftriggerClazz = triggerClazz;
		
		System.out.println("addPopupMenu " + popupmenuid + " " + pluginid + " " + menulabel + " " + menuid + " " + commandid + " " + triggerClazz.getName());

		AbstractContributionFactory menucontribution =
			   new AbstractContributionFactory(popupmenuid, pluginid) {
			

				@Override
				public void createContributionItems(IServiceLocator serviceLocator,
						IContributionRoot additions) {
					
					CommandContributionItemParameter contributionParameters = 
				    	new CommandContributionItemParameter(serviceLocator, fmenuid, fcommandid, 
				    			fparameters, null, null, null, fmenulabel, null, null, 
				    			CommandContributionItem.STYLE_PUSH, null, false);
				    
					CommandContributionItem item = new CommandContributionItem(contributionParameters );
					item.setVisible(true);
					additions.addContributionItem(item, new Expression() {
						
						@Override
						public EvaluationResult evaluate(IEvaluationContext context)
								throws CoreException {
							boolean isclazz = false;
							Object obj = context.getVariable("selection");
							if(obj instanceof IStructuredSelection) {
								IStructuredSelection selection = (IStructuredSelection) obj;
								if(ftriggerClazz.isInstance(selection.getFirstElement()))
								{
									isclazz = true;
								}
							}
							return EvaluationResult.valueOf(isclazz);
						}
					});
				}
		};
		
		service.addContributionFactory(menucontribution);
	}

	public static void addMenu(String popupmenuid, String pluginid, String menulabel, String menuid, String commandid, Map parameters) {
		IMenuService service = (IMenuService) PlatformUI.getWorkbench().getService(IMenuService.class);
		final Map fparameters = parameters;
	    final String fmenulabel = menulabel;
		final String fcommandid = commandid;
		final String fmenuid = menuid;
		
		AbstractContributionFactory menucontribution =
			   new AbstractContributionFactory(popupmenuid, pluginid) {
			

				@Override
				public void createContributionItems(IServiceLocator serviceLocator,
						IContributionRoot additions) {
					
					CommandContributionItemParameter contributionParameters = 
				    	new CommandContributionItemParameter(serviceLocator, fmenuid, fcommandid, 
				    			fparameters, null, null, null, fmenulabel, null, null, 
				    			CommandContributionItem.STYLE_PUSH, null, false);
				    
					CommandContributionItem item = new CommandContributionItem(contributionParameters );
					item.setVisible(true);
					additions.addContributionItem(item, new Expression() {
						
						@Override
						public EvaluationResult evaluate(IEvaluationContext context)
								throws CoreException {
							return EvaluationResult.valueOf(true);
						}
					});
				}
		};
		
		service.addContributionFactory(menucontribution);
	}

	public static void errorMessage(String message) {
		MessageBox messageBox = new MessageBox(new Shell(), SWT.OK);
		messageBox.setMessage(message);
		messageBox.setText("Error");
		messageBox.open();
//		ErrorDialog.openError(new Shell(), "Error", message, errormessage, displayMask);
	}
	
	public static boolean confirmMessage(String title, String message) {
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		return MessageDialog.openConfirm(shell, title, message);
	}
	
	public static boolean questionMessage(String title, String message) {
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		return MessageDialog.openQuestion(shell, title, message);
	}
	
	static public File getConfigurationFile() {
		String fileloc = Platform.getLocation().toString() + "/configurations.xml";
        try {
        	File confFile  = new File(fileloc);
        	boolean newfile = confFile.createNewFile();
        	if(newfile) {
				Element root = new Element("configuration");
				Document doc = new Document(root);
				FileOutputStream stream = new FileOutputStream(confFile);
	    		XMLOutputter output = new XMLOutputter();
	    		output.output(doc, stream);
	    		stream.close();
        	} else {
        		
        	}
        	
        	return confFile;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
	}
	
	static public void setConfiguration(Element element) {
        try {
        	File confFile = Util.getConfigurationFile();
			FileInputStream fileinputstream = new FileInputStream(confFile);
	        Document doc = null;
	        SAXBuilder sb = new SAXBuilder();
            doc = sb.build(fileinputstream);
            Element root = doc.getRootElement();
            // ensure element is unique by removing all other matching elements
			root.removeContent(new ElementFilter(element.getName()));
            // scan elements for possible duplicates
        	root.addContent(element);
			FileOutputStream stream = new FileOutputStream(confFile);
    		XMLOutputter output = new XMLOutputter();
    		output.output(doc, stream);
    		fileinputstream.close();
    		stream.close();
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	static public void setConfiguration(final Element element, final String uniqueField) {
        try {
        	File confFile = Util.getConfigurationFile();
			FileInputStream fileinputstream = new FileInputStream(confFile);
	        Document doc = null;
	        SAXBuilder sb = new SAXBuilder();
            doc = sb.build(fileinputstream);
            Element root = doc.getRootElement();
            if(uniqueField != null) {
	            root.removeContent(new Filter() {
					
					@Override
					public boolean matches(Object obj) {
						if(obj instanceof Element) {
							Element checkelement = (Element) obj;
							if(checkelement.getName().contentEquals(element.getName()) && checkelement.getAttribute(uniqueField).getValue().contentEquals(element.getAttribute(uniqueField).getValue())) {
								return true;
							}
						}
						return false;
					}
				});
            }
            // scan elements for possible duplicates
        	root.addContent(element);
			FileOutputStream stream = new FileOutputStream(confFile);
    		XMLOutputter output = new XMLOutputter();
    		output.output(doc, stream);
    		fileinputstream.close();
    		stream.close();
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	static public List getConfigurationElements(String name) {
        try {
        	File confFile = Util.getConfigurationFile();
			FileInputStream fileinputstream = new FileInputStream(confFile);
	        Document doc = null;
	        SAXBuilder sb = new SAXBuilder();
            doc = sb.build(fileinputstream);
            Element root = doc.getRootElement();
            fileinputstream.close();
            return root.getChildren(name);
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
	}
	
	static public Element getConfigurationElement(String name, String attributeName, String attributeValue) {
        try {
        	for(Object obj : Util.getConfigurationElements(name)) {
        		Element confelement = (Element) obj;
        		if(confelement.getAttribute(attributeName).getValue().contentEquals(attributeValue)) {
        			return confelement;
        		}
        	}
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return null;
	}
	
	static public String getUniqueConfigurationElementValue(String name, String attributeName) {
        try {
        	List elements = Util.getConfigurationElements(name);
        	if((elements != null) && (elements.size() > 0)) {
        		Element confelement = (Element) elements.get(0);
        		return confelement.getAttribute(attributeName).getValue();
        	}
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return null;
	}
	
	static public boolean setUniqueConfigurationElementValue(String name, String attributeName, String attributeValue) {
        try {
        	File confFile = Util.getConfigurationFile();
			FileInputStream fileinputstream = new FileInputStream(confFile);
	        Document doc = null;
	        SAXBuilder sb = new SAXBuilder();
            doc = sb.build(fileinputstream);
            Element root = doc.getRootElement();
            List<Element> elements = root.getChildren(name);
            Element confelement = null;
        	if((elements != null) && (elements.size() > 0)) {
        		confelement = (Element) elements.get(0);
        	} else {
        		confelement = new Element(name);
        		root.addContent(confelement);
        	}
        	confelement.setAttribute(attributeName, attributeValue);
			FileOutputStream stream = new FileOutputStream(confFile);
    		XMLOutputter output = new XMLOutputter();
    		output.output(doc, stream);
    		stream.close();
    		fileinputstream.close();
            return true;
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
	}

}
