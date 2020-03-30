package com.liferay.rbi.command;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate=true,
	    property = { 
	        "javax.portlet.name=" + JournalPortletKeys.JOURNAL, 
	        "mvc.command.name=/journal/add_article", 
	        "mvc.command.name=/journal/update_article",
	        "service.ranking:Integer=400" 
	    }, 
	    service = MVCActionCommand.class
	)
public class CustomJournalMVCCommand extends BaseMVCActionCommand {

	
	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {
		
		System.out.println("Inside Journal Add action command  ");
		
		ThemeDisplay themeDisplay= (ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		DLFolder dlFolder=null;
		//	DLFolder dlFolder=DLFolderLocalServiceUtil.getFolder(themeDisplay.getScopeGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "PDFImages");
		
			//System.out.println("Folder ID:"+ dlFolder.getFolderId());
			List<DLFolder> dlFolders=   DLFolderLocalServiceUtil.getFolders(themeDisplay.getScopeGroupId(),DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		
			boolean isFolderAvailable=false;
		for(DLFolder temp:dlFolders)
		{
			if(temp.getName().equals("PDFImages"))
			{
				System.out.println("Folder exists");
				isFolderAvailable=true;
				dlFolder=temp;
			}
			
		}
		
		if(!isFolderAvailable)
		{
			dlFolder= DLFolderLocalServiceUtil.createDLFolder(CounterLocalServiceUtil.increment(DLFolder.class.getName()));
			dlFolder.setName("PDFImages");
			
		}
		
		String folderPath=   DLURLHelperUtil.getFolderControlPanelLink(actionRequest, dlFolder.getFolderId());
		
		System.out.println("FolderPath"+folderPath);
		
		User user=  themeDisplay.getUser();
		
		if(user.getExpandoBridge().getAttribute("FolderPath").toString()==null)
		{
		user.getExpandoBridge().setAttribute("FolderPath",folderPath);
		}
		mvcActionCommand.processAction(actionRequest, actionResponse);
				}
	
		
@Reference(target = "(component.name=com.liferay.journal.web.internal.portlet.action.UpdateArticleMVCActionCommand)")
    protected MVCActionCommand mvcActionCommand;


/*@Reference
DLURLHelperUtil dlurlHelperUtil;

@Reference
private DLFolderLocalService dlFolderLocalService;


@Reference
private CounterLocalService counterLocalService;
*/
}

