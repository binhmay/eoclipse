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
package com.metaaps.eoclipse.viewers.util;

import java.awt.Color;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.metaaps.eoclipse.common.Property;

/**
 * @author leforthomas
 * 
 * Utility class for editing values with a User Interface
 * 
 */
public class EditValueDialog {

	public static Property open(Property property) {
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		if(property.getValue() instanceof Boolean) {
			property.setValue(new Boolean(!((Boolean) property.getValue()).booleanValue()));
			return property;
		}
		if(property.getValue() instanceof String) {
			InputDialog dialog = new InputDialog(shell, "Edit Property", "Change Value of Property " + property.getProperty(), (String) property.getValue(), new IInputValidator() {
				
				@Override
				public String isValid(String newText) {
					if(newText.length() == 0) return "You need to enter a Value!";
					return null;
				}
			});
			dialog.open();
			property.setValue(dialog.getValue());
			return property;
		}
		if(property.getValue() instanceof Integer) {
			InputDialog dialog = new InputDialog(shell, "Edit Property", "Change Value of Property " + property.getProperty(), ((Integer) property.getValue()).toString(), new IInputValidator() {
				
				@Override
				public String isValid(String newText) {
					try {
						Integer.parseInt(newText);
					} catch(NumberFormatException e) {
						return "You need to enter a Value!";
					}
					return null;
				}
			});
			dialog.open();
			property.setValue(Integer.parseInt(dialog.getValue()));
			return property;
		}
		if(property.getValue() instanceof Color) {
			ColorDialog colordialog = new ColorDialog(shell);
			colordialog.open();
			RGB rgb = colordialog.getRGB();
			property.setValue(new Color(rgb.red, rgb.green, rgb.blue));
			return property;
		}
		return null;
	}
	
	public static String format(Object value) {
		if(value instanceof Boolean) {
			return (((Boolean) value).booleanValue() ? "TRUE" : "FALSE");
		}
		if(value instanceof String) {
			return (String) value;
		}
		if(value instanceof Integer) {
			return ((Integer) value).toString();
		}
		if(value instanceof Color) {
			return "#" + Integer.toHexString(((Color) value).hashCode()).toUpperCase();
		}
		return value.toString();
	}



}
