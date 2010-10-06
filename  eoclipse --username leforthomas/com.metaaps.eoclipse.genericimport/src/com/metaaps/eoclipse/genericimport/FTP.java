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
package com.metaaps.eoclipse.genericimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;

import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.datasets.IImport;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTP implements IImport {

	private String m_filename;
	private String m_uri;
	private boolean m_keeptrying;

	public FTP() {
	}

	/*
	 * Download file using the FTP protocol with the specified URI
	 * 
	 */
	@Override
	public File importFile(IProgressMonitor monitor) throws Exception {
		FTPClient ftpClient = FTP.connect(m_uri);
		ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		ftpClient.enterLocalPassiveMode();
		// create a local copy of the file
		String fullpath = new URI(m_uri).getPath().replaceAll("^/", "");
		String cachefileloc = Platform.getLocation().toString() + "/downloads/ftp/" + fullpath;
		File file = new File(cachefileloc);
		file.createNewFile();
		OutputStream outputstream = new FileOutputStream(file); //.openOutputStream(EFS.OVERWRITE, monitor);

		InputStream stream = null;
		// check file exists
		{
			try {
				stream = ftpClient.retrieveFileStream(fullpath);
				if(monitor.isCanceled()) {
					m_keeptrying = false;
				}
				monitor.beginTask("Could not find file...", 0);
			} catch(Exception e) {
				
			}
		} while((stream == null) && m_keeptrying) ;
		monitor.beginTask("Creating temp file...", 10);
		if(stream == null) {
			ftpClient.disconnect();
			outputstream.close();
			throw new Exception("Could not retrieve file, error code: " + ftpClient.getReplyCode());
		}
		stream.close();
		
		ftpClient.setSoTimeout(10000);
		monitor.beginTask("Now Downloading file...", 20);
		if(ftpClient.retrieveFile(fullpath, outputstream) == false) {
			ftpClient.disconnect();
			outputstream.close();
			throw new Exception("Could not retrieve all file.");
		}

		if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			outputstream.close();
			ftpClient.disconnect();
			throw new Exception("File Download not completed.");
		}
		
		monitor.beginTask("File Downloaded...", 80);
		ftpClient.disconnect();
		outputstream.close();
		return new File(cachefileloc);
	}

	@Override
	public void useOwnImport(IDataSets datasets) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getURI() {
		return m_uri;
	}

	@Override
	public void setURI(String URI) {
		m_uri = URI;
	}

	public static FTPClient connect(String uri) throws Exception {
		FTPClient ftpClient = new FTPClient();
		ftpClient.addProtocolCommandListener(new ProtocolCommandListener() {
			
			@Override
			public void protocolReplyReceived(ProtocolCommandEvent arg0) {
				System.out.println(arg0.getCommand() + " " + arg0.getMessage());
			}
			
			@Override
			public void protocolCommandSent(ProtocolCommandEvent arg0) {
				System.out.println(arg0.getCommand() + " " + arg0.getMessage());
			}
		});
		URI ftpURI = new URI(uri);
		ftpClient.connect(ftpURI.getHost(), ftpURI.getPort());
		String[] userinfo = ftpURI.getUserInfo().split(":");
		ftpClient.login(userinfo[0], userinfo[1]);
		if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			ftpClient.disconnect();
			throw new Exception("Could not connect to server. FTP server refused connection.");
		}
		
		return ftpClient;
		
	}

	public void setKeeptrying(boolean keepretrying) {
		m_keeptrying = keepretrying;
	}

	public void setKeepLocalCopy(boolean keeplocalcopy) {
		// TODO Auto-generated method stub
		
	}

}
