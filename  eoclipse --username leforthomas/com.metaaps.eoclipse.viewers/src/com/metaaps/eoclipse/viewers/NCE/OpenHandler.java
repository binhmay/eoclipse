package com.metaaps.eoclipse.viewers.NCE;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;

import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.viewers.Viewer;

public class OpenHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        ITreeSelection currentSelection = (ITreeSelection)HandlerUtil.getCurrentSelection(event);
//        IStructuredSelection currentSelection = (IStructuredSelection)HandlerUtil.getCurrentSelection(event);
        Object obj = currentSelection.iterator().next();
        if(obj instanceof Viewer)
        {
            IWorkFlow workflow = (IWorkFlow) Util.scanTreePath(currentSelection, IWorkFlow.class);
        	((Viewer)obj).Open(workflow);
        }
        
		return null;
	}

}
