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
		if(value instanceof Color) {
			return "#" + Integer.toHexString(((Color) value).hashCode()).toUpperCase();
		}
		return value.toString();
	}



}
