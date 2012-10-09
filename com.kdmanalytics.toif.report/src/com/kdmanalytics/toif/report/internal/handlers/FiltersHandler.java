/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kdmanalytics.toif.report.internal.filters.CWETwoToolsFilter;
import com.kdmanalytics.toif.report.internal.filters.IsValidFilter;
import com.kdmanalytics.toif.report.internal.filters.NotValidFilter;
import com.kdmanalytics.toif.report.internal.filters.SFPTwoToolsFilter;
import com.kdmanalytics.toif.report.internal.filters.TermFilter;
import com.kdmanalytics.toif.report.internal.filters.TrustFilter;
import com.kdmanalytics.toif.report.internal.filters.TwoToolsFilter;
import com.kdmanalytics.toif.report.internal.views.ReportView;

/**
 * handler for the filters in the report view.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class FiltersHandler extends AbstractHandler implements IHandler
{
    
    /**
     * create the filter dialog and execute the handling of the filters. add the
     * filter list to the view and refresh
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Shell parentShell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
        final ReportView view = (ReportView) HandlerUtil.getActivePart(event);
        
        FiltersDialog dialog = new FiltersDialog(parentShell, view.getFilters());
        
        dialog.open();
        
        List<ViewerFilter> filterList = new ArrayList<ViewerFilter>();
        
        // Hack to ensure that the term filter is applied in addition to the
        // filters that are handled by the dialog
        for (ViewerFilter filter : view.getFilters())
        {
            if (filter instanceof TermFilter)
            {
                filterList.add(filter);
            }
        }
        
        handleTrustFilter(dialog, filterList);
        
        handleTwoToolFilter(dialog, filterList);
        handleCWETwoToolFilter(dialog, filterList);
        handleSFPTwoToolFilter(dialog, filterList);
        
        handleIsValidFilter(dialog, filterList);
        handleNotValidFilter(dialog, filterList);
        
        view.setFilters(filterList.toArray(new ViewerFilter[filterList.size()]));
        view.refresh();
        return null;
    }
    
    /**
     * handle the not valid filter. by adding it to the filterList.
     * 
     * @param dialog
     * @param filterList
     */
    private void handleNotValidFilter(FiltersDialog dialog, List<ViewerFilter> filterList)
    {
        NotValidFilter notValidFilter = dialog.getNotValidFilter();
        if (notValidFilter != null)
        {
            filterList.add(notValidFilter);
        }
        
    }
    
    /**
     * handle the same cwe filter by adding it to the filterList.
     * 
     * @param dialog
     * @param filterList
     */
    private void handleCWETwoToolFilter(FiltersDialog dialog, List<ViewerFilter> filterList)
    {
        CWETwoToolsFilter cweTwoToolsFilter = dialog.getCWETwoToolsFilter();
        if (cweTwoToolsFilter != null)
        {
            filterList.add(cweTwoToolsFilter);
        }
        
    }
    
    /**
     * handle the same sfp filter by adding it to the filterList.
     * 
     * @param dialog
     * @param filterList
     */
    private void handleSFPTwoToolFilter(FiltersDialog dialog, List<ViewerFilter> filterList)
    {
        SFPTwoToolsFilter sfpTwoToolsFilter = dialog.getSFPTwoToolsFilter();
        if (sfpTwoToolsFilter != null)
        {
            filterList.add(sfpTwoToolsFilter);
        }
        
    }
    
    /**
     * handle the trust filter by adding it to the filterList.
     * 
     * @param dialog
     * @param filterList
     */
    private void handleTrustFilter(FiltersDialog dialog, List<ViewerFilter> filterList)
    {
        TrustFilter trustFilter = dialog.getTrustFilter();
        if (trustFilter != null)
        {
            trustFilter.setAmount(dialog.getTrustAmount());
            filterList.add(trustFilter);
        }
    }
    
    /**
     * handle the two tools filter by adding it to the filterList.
     * 
     * @param dialog
     * @param filterList
     */
    private void handleTwoToolFilter(FiltersDialog dialog, List<ViewerFilter> filterList)
    {
        TwoToolsFilter twoToolsFilter = dialog.getTwoToolsFilter();
        if (twoToolsFilter != null)
        {
            filterList.add(twoToolsFilter);
        }
    }
    
    /**
     * handle the is valid filter by adding it to the filterList.
     * 
     * @param dialog
     * @param filterList
     */
    private void handleIsValidFilter(FiltersDialog dialog, List<ViewerFilter> filterList)
    {
        IsValidFilter isValidFilter = dialog.getIsValidFilter();
        if (isValidFilter != null)
        {
            filterList.add(isValidFilter);
        }
    }
}
