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
package com.metaaps.eoclipse.datasets.internal.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import com.metaaps.eoclipse.common.datasets.IImport;
import com.metaaps.eoclipse.common.datasets.IImportWizardPage;
import com.metaaps.eoclipse.common.datasets.IReader;
import com.metaaps.eoclipse.datasets.readers.ReadersFolder;

/**
 * 
 * @author leforthomas
 * 
 * The wizard for importing data
 * The wizard is triggered when data is imported through the application GUI
 * The wizard is built in three pages, ie the reader selection (format), the import method (choose the file) and an optional name selection page with possibly other additions
 * 
 */
public class ImportDataWizard extends Wizard {

	private IImportWizardPage m_importpage;
//	private ReaderDataPage m_readerpage;
	private AdditionalInfoWizardPage m_additionalpage;

	public ImportDataWizard(IImportWizardPage page) {
		super();
		setNeedsProgressMonitor(true);
		m_importpage = page;
//		m_readerpage = new ReaderDataPage();
		m_additionalpage = new AdditionalInfoWizardPage((IImportWizardPage)m_importpage);
	}

	@Override
	public void addPages() {
//		addPage(m_readerpage);
		addPage((WizardPage) m_importpage);
		m_importpage.setFilter(ReadersFolder.getInstance().getFilters());
		addPage(m_additionalpage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public IReader getReader() {
		return m_additionalpage.getReader();
//		return m_readerpage.getReader();
	}

	public IImport getImportMethod() {
		return ((IImportWizardPage)m_importpage).getImportMethod();
	}

	public String getDataName() {
		return m_additionalpage.getName();
	}
	
}
