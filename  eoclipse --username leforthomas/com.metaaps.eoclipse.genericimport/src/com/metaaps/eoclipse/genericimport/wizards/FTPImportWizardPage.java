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
package com.metaaps.eoclipse.genericimport.wizards;

import java.awt.Checkbox;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IImport;
import com.metaaps.eoclipse.common.datasets.IImportWizardPage;
import com.metaaps.eoclipse.genericimport.FTP;
import com.metaaps.eoclipse.genericimport.LocalFile;

public class FTPImportWizardPage extends WizardPage implements IImportWizardPage {
	private static final String CONFIGURATION_NAME = "com.metaaps.eoclipse.genericimport.ftp.configuration";
	private Composite container;
	private String m_uri;
	private Text m_host;
	private Text m_username;
	private Text m_password;
	private Text m_port;
	private Text m_filepath;
	private Combo m_configurations;
	private List<Element> m_configurationslist;
	private Button m_retry;
	private Button m_checkbutton;
	private Text m_status;
	private Button m_localcopy;
	private FTP m_method;
	private String m_filter;

	public FTPImportWizardPage() {
		super("Import Data Source From Local Disks");
		setTitle("Select a Data Source From Local Files");
		setDescription("Select a Data Source to Include in the Work Flow");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		
        m_configurationslist = Util.getConfigurationElements(CONFIGURATION_NAME);
		
		new Label(container, SWT.NULL).setText("Existing Configurations");
		m_configurations = new Combo(container, SWT.SIMPLE | SWT.READ_ONLY);
		m_configurations.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_configurations.add("New");
		if(m_configurationslist != null) {
	        for(Element configuration : m_configurationslist) {
	        	m_configurations.add(configuration.getAttributeValue("name"));
	        }
		}
		m_configurations.select(0);
        m_configurations.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String name = m_configurations.getText();
				if(m_configurationslist != null) {
			        for(Element configuration : m_configurationslist) {
			        	if(name.contentEquals(configuration.getAttributeValue("name"))) {
			        		m_host.setText(configuration.getAttributeValue("host"));
			        		m_password.setText(configuration.getAttributeValue("password"));
			        		m_port.setText(configuration.getAttributeValue("port"));
			        		m_username.setText(configuration.getAttributeValue("username"));
			        	}
			        }
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
        
        ModifyListener listener = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if((m_host.getText().length() > 0) && (m_filepath.getText().length() > 0) && (m_password.getText().length() > 0) &&(m_port.getText().length() > 0) && (m_username.getText().length() > 0)) {
					m_checkbutton.setEnabled(true);
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		};

		new Label(container, SWT.NULL).setText("Host");
		m_host = new Text(container, SWT.SINGLE | SWT.BORDER);
		m_host.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_host.addModifyListener(listener);

		new Label(container, SWT.NULL).setText("User Name");
		m_username = new Text(container, SWT.SINGLE | SWT.BORDER);
		m_username.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_username.addModifyListener(listener);

		new Label(container, SWT.NULL).setText("Password");
		m_password = new Text(container, SWT.SINGLE | SWT.BORDER);
		m_password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_password.addModifyListener(listener);

		new Label(container, SWT.NULL).setText("Port");
		m_port = new Text(container, SWT.SINGLE | SWT.BORDER);
		m_port.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_port.addModifyListener(listener);

		new Label(container, SWT.NULL).setText("Path");
		m_filepath = new Text(container, SWT.SINGLE | SWT.BORDER);
		m_filepath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_filepath.addModifyListener(listener);

		m_checkbutton = new Button(container, SWT.PUSH);
		m_checkbutton.setText("Check");
		m_checkbutton.setEnabled(false);
		m_status = new Text(container, SWT.NULL | SWT.READ_ONLY);
		m_status.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_status.setText("");
		m_checkbutton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				m_status.setText("Checking connection parameters...");
				m_status.redraw();
				String uri = getURI();
				String result = "";
				try {
					FTPClient ftpClient = FTP.connect(uri);
					if(ftpClient != null) {
						String fullpath = new URI(uri).getPath();
//						FTPFile[] files = ftpClient.listFiles();
//						for(FTPFile file : files) {
//							System.out.println(file.getName());
//						}
						InputStream stream = ftpClient.retrieveFileStream(fullpath.replaceAll("^/", ""));
						if(stream == null) {
							ftpClient.disconnect();
							throw new Exception("Could not retrieve file, error code: " + ftpClient.getReplyCode());
						}
						stream.close();
						result = "Parameters OK";
						ftpClient.disconnect();
					}
				} catch (Exception e) {
					result = "Error: " + e.getMessage();
				}
				m_status.setText(result);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		new Label(container, SWT.NULL).setText("Keep Retrying");
		m_retry = new Button(container, SWT.CHECK);
		
		new Label(container, SWT.NULL).setText("Use Local Copy");
		m_localcopy = new Button(container, SWT.CHECK);
		
//		new Label(container, SWT.NULL).setText("Save Configuration as");
//		m_confname = new Text(container, SWT.SINGLE | SWT.BORDER);
//		m_confname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
		
	}
	
	@Override
	public void dispose() {
		String uri = getURI();
		if(m_configurations.getText().contentEquals("New")) {
        	Element configuration = new Element("com.metaaps.eoclipse.genericimport.ftp.configuration");
            String confname = m_host.getText() + m_username.getText();
            configuration.setAttribute("name", confname);
            configuration.setAttribute("host", m_host.getText());
            configuration.setAttribute("username", m_username.getText());
            configuration.setAttribute("port", m_port.getText());
            configuration.setAttribute("password", m_password.getText());
			Util.setConfiguration(configuration, "name");
		}
		FTP method = new FTP();
		method.setURI(uri);
		method.setKeeptrying(m_retry.getSelection());
		method.setKeepLocalCopy(m_localcopy.getSelection());
		m_method = method;
		
		super.dispose();
	}

	private String getURI() {
		return "ftp://" + m_username.getText() + ":" + m_password.getText() + "@" + m_host.getText() + ":" + m_port.getText() + "/" + m_filepath.getText();
	}

	@Override
	public IImport getImportMethod() {
		return m_method;
	}
	
	@Override
	public void setFilter(String filter) {
		m_filter = filter;
	}
	
}

