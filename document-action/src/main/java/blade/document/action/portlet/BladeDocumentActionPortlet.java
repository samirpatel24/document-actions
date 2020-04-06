/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package blade.document.action.portlet;

import com.bcl.easyconverter.html.PDF2HTML;
import com.bcl.easyconverter.html.PDF2HTMLException;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.portlet.GenericPortlet;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author liferay
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=BladeDocumentAction Portlet",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class BladeDocumentActionPortlet extends GenericPortlet {

	@Override
	protected void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PrintWriter printWriter = renderResponse.getWriter();

		long fileEntryId =Long.parseLong(renderRequest.getParameter("fileEntryId"));
		String fileName = renderRequest.getParameter("fileName");
		String mimeType = renderRequest.getParameter("mimeType");
		String version = renderRequest.getParameter("version");
		String statusLabel = renderRequest.getParameter("statusLabel");
		String createdDate = renderRequest.getParameter("createdDate");
		String createdUserName = renderRequest.getParameter("createdUserName");

		
		ThemeDisplay themeDisplay= (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		 DLFileEntry dlFileEntry= dlFileEntryLocalService.fetchDLFileEntry(fileEntryId);
		 
		printWriter.print(
				"<span style=\"color:green\">File ID</span>:" + fileEntryId +
					"<br/>");
		printWriter.print(
			"<span style=\"color:green\">File Name</span>:" + fileName +
				"<br/>");
		printWriter.print(
			"<span style=\"color:green\">Type</span>:" + mimeType + "<br/>");
		printWriter.print(
			"<span style=\"color:green\">Version</span>:" + version + "<br/>");
		printWriter.print(
			"<span style=\"color:green\">Status</span>:" + statusLabel +
				"<br/>");
		printWriter.print(
			"<span style=\"color:green\">Created Date</span>:" + createdDate +
				"<br/>");
		
		
		String htmlContent= getPDFConvertedtoHTML(dlFileEntry);
		
		if(articleLocalService.fetchArticleByUrlTitle(themeDisplay.getScopeGroupId(),dlFileEntry.getTitle()) != null)
		{
			JournalArticle journalArticle= articleLocalService.fetchArticleByUrlTitle(themeDisplay.getScopeGroupId(),dlFileEntry.getFileName());
				updateArticle(journalArticle,htmlContent);
				
		}else
		{
		JournalArticle addArticle=	addArticle(htmlContent, themeDisplay,dlFileEntry,renderRequest);
			printWriter.print(
					"<h1><center><span style=\"color:green\">HTML is created. Please check in Web Content section with the name </span>:" +
						 addArticle.getTitle()+ "<br/></h1>");
			
		}
	   
		
	}
	
	private JournalArticle addArticle(String htmlContent, ThemeDisplay themeDisplay,DLFileEntry dlFileEntry, RenderRequest renderRequest) {
		
		JournalArticle addArticle= null;
		
		// TODO Auto-generated method stub
		System.out.println("Ädding a New Article");
		 Map<Locale,String> titleMap= new HashMap<Locale, String>();
		    
	
		    titleMap.put(Locale.US, dlFileEntry.getTitle());

		    Map<Locale,String> descriptionMap= new HashMap<Locale, String>();
		    descriptionMap.put(Locale.getDefault(), ""); 
		     
		    Map<Locale,String> friendlyURLMap= new HashMap<Locale, String>();
		    descriptionMap.put(Locale.getDefault(), " ");
		    
			List<DDMStructure> ddmStructures= ddmStructureLocalService.getDDMStructures(0, ddmStructureLocalService.getDDMStructuresCount());
		    
			DDMStructure ddmStructure=null;
			
			for(DDMStructure temp: ddmStructures)
			{
			String name=	 temp.getName(Locale.US);
				if(name.equals("RBI Research Paper"))
				{
				ddmStructure=temp;	
				}
			}
			
			List<DDMTemplate> ddmTemplates =ddmTemplateLocalService.getDDMTemplates(0, ddmTemplateLocalService.getDDMTemplatesCount());
			
			DDMTemplate ddmTemplate=null;
			for(DDMTemplate temp: ddmTemplates)
			{
				String name = temp.getName(Locale.US);
				 if(name.equals("RBI Research Paper Template"))
				 {
					 ddmTemplate=temp;
				 }
			}
			
		      ServiceContext serviceContext = null;
			try {
				serviceContext = ServiceContextFactory.getInstance(JournalArticle.class.getName(), renderRequest);
			} catch (PortalException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			serviceContext.setScopeGroupId(themeDisplay.getScopeGroupId());
		String  journalContent=	createDocumentXML(htmlContent, themeDisplay,dlFileEntry);
		
	    GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance(TimeZone.getDefault(),Locale.ENGLISH);

		
		System.out.println("JournalArticle Title is "+titleMap);
		//String articleId=      articleLocalService.cre
		//System.out.println("Article ID"+articleId);
		System.out.println("ServiceContext : "+serviceContext.getUuid());
		try {

			 addArticle=articleLocalService.addArticle(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), 0L, 0L, 0L, null,true, 1D,
			titleMap, descriptionMap,friendlyURLMap, journalContent,ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),null,
			 date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
    		date.get(Calendar.YEAR), date.get(Calendar.HOUR), date.get(Calendar.MINUTE), 0, 0, 
			0, 0, 0, true, 0, 0, 0,
			0, 0, true, true, false, null, null, null, null, serviceContext);
	
	
			System.out.println("Ädded article is"+addArticle.getTitle() );
			
			
			//assetEntryLocalService.add
  
  
} catch (PortalException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
		
		return addArticle;
	
	}

	private String createDocumentXML(String htmlContent, ThemeDisplay themeDisplay, DLFileEntry dlFileEntry) {
		// TODO Auto-generated method stub
		Locale locale = LocaleUtil.getSiteDefault();

	    Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		rootElement.addAttribute("available-locales", locale.toString());
		rootElement.addAttribute("default-locale", locale.toString());
		
		String key = PwdGenerator.getPassword(4);

		Element dynamicTitleElement = rootElement.addElement("dynamic-element");
		dynamicTitleElement.addAttribute("index-type", "keyword");
		dynamicTitleElement.addAttribute("name", "Title");
		dynamicTitleElement.addAttribute("type", "text");
		dynamicTitleElement.addAttribute("instance-id", key);
		
		Element  elementDynamicTitleContent= dynamicTitleElement.addElement("dynamic-content");
		elementDynamicTitleContent.addAttribute("language-id", LocaleUtil.toLanguageId(locale));		
		elementDynamicTitleContent.addCDATA(dlFileEntry.getTitle());
		
		String key2 = PwdGenerator.getPassword(4);

		Element dynamicDocumentElement = rootElement.addElement("dynamic-element");
		dynamicDocumentElement.addAttribute("name", "UploadResearchDocument");
		dynamicDocumentElement.addAttribute("index-type", "keyword");
		dynamicDocumentElement.addAttribute("type", "document_library");
		dynamicDocumentElement.addAttribute("instance-id",key2);
		
		Element elementDynamicDocumentContent =   dynamicDocumentElement.addElement("dynamic-content");
		
		elementDynamicDocumentContent.addAttribute("language-id", LocaleUtil.toLanguageId(locale));
		
		JSONObject  jsonObject = JSONFactoryUtil.createJSONObject();
		 jsonObject.put("classPK", dlFileEntry.getFileEntryId());
		 jsonObject.put("groupId", themeDisplay.getScopeGroupId());
		 jsonObject.put("title", dlFileEntry.getTitle());
		 jsonObject.put("type","document");
		 jsonObject.put("uuid", dlFileEntry.getUuid());
		 
		 elementDynamicDocumentContent.addCDATA(jsonObject.toJSONString());
		
		 Element dynamicPDFRenderElement = rootElement.addElement("dynamic-element");
		 
		 String key3= PwdGenerator.getPassword(4);
		 
		 dynamicPDFRenderElement.addAttribute("name", "pdfContent");
		 dynamicPDFRenderElement.addAttribute("index-type", "text");
		 dynamicPDFRenderElement.addAttribute("type", "text_area");
		 dynamicPDFRenderElement.addAttribute("instance-id",key3);
		 
		Element elementDynamicPDFContent =  dynamicPDFRenderElement.addElement("dynamic-content");

		elementDynamicPDFContent.addAttribute("language-id", LocaleUtil.toLanguageId(locale));

		 elementDynamicPDFContent.addCDATA(htmlContent);
		 
		//System.out.println("XML created"+document.asXML());
		
		return document.asXML();
	}

	private void updateArticle(JournalArticle journalArticle, String htmlContent) {
		// TODO Auto-generated method stub
		
		System.out.println("Updating Article");
	}

	public String getPDFConvertedtoHTML(DLFileEntry dlFileEntry)
	{
		String webcontent=null;
		PDF2HTML converter = new PDF2HTML(); 	
		//File file = new File("C:/MyOutputDirectory/file.html");
		try {
		converter.setImageEmbedded(true);
		
		InputStream inputStream=   dlFileEntry.getContentStream();
		
		byte[] bInput=	IOUtils.toByteArray(inputStream);
		System.out.println(bInput.length);
		
		byte[][] output=  converter.ConvertToHTML3(bInput, "", -1, -1);
		
		 byte[] htmlStream = output[0];
		 String htmlString = new String(htmlStream, StandardCharsets.UTF_8);
		
		 StringBuilder sb = new StringBuilder();
		 org.jsoup.nodes.Document document=Jsoup.parse(htmlString);
		 
		   String style=   document.select("STYLE").first().data();
		   style =  "<style>"+style+"</style>";
		   
		   String javascript = document.select("script").first().data();
		   
		   javascript = "<script>"+javascript+"</script>";
		   
		   String body=	document.body().html();
		   
		   org.jsoup.nodes.Element Body =document.getElementsByAttribute("onload").first();
		   
		   String onLoadFunction = null;
		   if(Body.attr("onload")!=null)
		   {
		    onLoadFunction= Body.attr("onload");
		   }
		   
		   body = "<div onload=\""+onLoadFunction+"\">"+body+"</div>";
		   
		    
		   
		   webcontent = sb.append(javascript+body).toString();

		    System.out.println("complete content "+webcontent);
		     //htmlText= document.html();
		// System.out.println("After parsing"+htmlText);
		  
		 
		} catch (PDF2HTMLException | PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return webcontent;
	}

	@Reference 
	protected DLFileEntryLocalService dlFileEntryLocalService;
	
	@Reference
	protected JournalArticleLocalService articleLocalService;
	
	@Reference
	protected DDMStructureLocalService ddmStructureLocalService;
	
	@Reference
	protected DDMTemplateLocalService ddmTemplateLocalService; 
	
	@Reference
	protected AssetEntryLocalService assetEntryLocalService;
	
	@Reference
	protected CounterLocalService counterLocalService;

}