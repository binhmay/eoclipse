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
package com.metaaps.eoclipse;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.wireadmin.*;
import com.metaaps.eoclipse.common.IRendering;
import com.metaaps.eoclipse.common.ITools;

/**
 * @author leforthomas
 * 
 * This class is not used anymore
 * 
 */
public class PluginRegistry {
	
	private static ArrayList<Producer> m_producerServices = new ArrayList<Producer>();
	private static ArrayList<Consumer> m_consumerServices = new ArrayList<Consumer>();
	private static ArrayList<ITools> m_toolsServices = new ArrayList<ITools>();
	
	public static ArrayList<Producer> getProducerServices() {
		return m_producerServices;
	}

	public static ArrayList<Consumer> getConsumerServices() {
		return m_consumerServices;
	}

	public static ArrayList<ITools> getToolsServices() {
		return m_toolsServices;
	}

	//	private ArrayList<ToolsAction> m_toolsServices = new ArrayList<ToolsAction>();
	private ArrayList<RenderingAction> m_renderingServices = new ArrayList<RenderingAction>();
	private BundleContext m_context;
	
	class ToolsAction extends Action {
		private ITools m_tool = null;
		public ToolsAction(ITools tool)
		{
			m_tool = tool;
			setText("Action 1");
			setToolTipText("Action 1 tooltip");
			setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		}
		public void run() {
			
		}
		
	}
	
	class RenderingAction extends Action {
		private IRendering m_render = null;
		public RenderingAction(IRendering render)
		{
			m_render = render;
			setText(render.getActionText());
			setToolTipText("Action 1 tooltip");
			setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		}
		public void register() {
//			actionbars.setGlobalActionHandler(
//					IWorkbenchActionConstants.MB_ADDITIONS,
//					this);

		}
		
		public void run() {
			
		}
		
	}
	
	/**
	* The constructor could be made private
	* to prevent others from instantiating this class.
	* But this would also make it impossible to
	* create instances of Singleton subclasses.
	*/
	protected PluginRegistry(BundleContext context)
	{
		m_context = context;
		monitorList();
	}
	
	/**
	* A handle to the unique Singleton instance.
	*/
	static private PluginRegistry _instance = null;
	 
	/**
	* @return The unique instance of this class.
	*/
	static public PluginRegistry instance(BundleContext context) {
		if(null == _instance) {
			_instance = new PluginRegistry(context);
		}
		return _instance;
	}
	
	// keep track of producers and consumers services
	private void monitorList()
	{
		System.out.println("Scanning for plugins");
//		ServiceTracker tracker = new ServiceTracker(context, Producer.class.getName(), null);
//		tracker.open();
		
		ServiceListener listener = new ServiceListener() {
			
			@Override
			public void serviceChanged(ServiceEvent event) {
				  ServiceReference sr = event.getServiceReference();
				  Object service = m_context.getService(sr);
				  switch(event.getType()) {
					  case ServiceEvent.REGISTERED:
					    {
					    	registerService(service);
					    	System.out.println("Registered service from bundle: " + sr.getBundle().getHeaders().get("Bundle-Name") + " with ID " + sr.getBundle().getBundleId());
					    }
					    break;
					  case ServiceEvent.UNREGISTERING:
					    {
					    	unRegisterService(service);
							System.out.println("Unregistered service from bundle: " + sr.getBundle().getHeaders().get("Bundle-Name"));
					    }
					    break;
				  }
			}
		};
		
		try {
			m_context.addServiceListener(listener, "(|(objectclass=org.osgi.service.wireadmin.Producer)(objectclass=org.osgi.service.wireadmin.Consumer)(objectclass=com.metaaps.eoclipse.services.ITools)(objectclass=com.metaaps.eoclipse.services.IRendering))");
			activateServices(new Class[]{Producer.class, ITools.class}, listener);
		} catch (InvalidSyntaxException e) { 
		  e.printStackTrace();
		}
	}
	
	private void activateServices(Class[] classes, ServiceListener listener) throws InvalidSyntaxException {
		// TODO Auto-generated method stub
		for(Class clazz : classes)
		{
			ServiceReference[] srl = m_context.getServiceReferences(clazz.getName(), null);
			// now trigger activation events for plugins already activated
			for(int i = 0; srl != null && i < srl.length; i++) {
				listener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, srl[i]));
			}
		}
	}

	private void registerService(Object service)
	{
    	if(service instanceof Producer)
    	{
	    	m_producerServices.add((Producer)service);
    	}
    	if(service instanceof Consumer)
    	{
	    	m_consumerServices.add((Consumer)service);
    	}
    	if(service instanceof ITools)
    	{
    		ITools tool = (ITools)service;
			m_toolsServices.add(tool);
			// create action and add menu and button in the toolbar
			
			// initialise tool with workbench for access to graphical resources
//			tool.registerWorkbench((IWorkbenchUtility)m_context.getService(m_context.getServiceReference(IWorkbenchUtility.class.getName())));
    		// take extra actions such as add tool action in toolbar
    	}
    	if(service instanceof IRendering)
    	{
    		IRendering render = (IRendering)service;
			m_renderingServices.add(null); //new RenderingAction(render));
			// create action and add menu and button in the toolbar
    	}
	}
	
	private void unRegisterService(Object service)
	{
    	if(service instanceof Producer)
    	{
	    	if(m_producerServices.remove(service) == false)
	    	{
	    		System.out.println("Service not found");
	    	}
    	}
    	if(service instanceof Consumer)
    	{
	    	if(m_consumerServices.remove(service) == false)
	    	{
	    		System.out.println("Service not found");
	    	}
    	}
    	if(service instanceof ITools)
    	{
	    	if(m_toolsServices.remove(service) == false)
	    	{
	    		System.out.println("Service not found");
	    	}
    	}
		
	}

}
