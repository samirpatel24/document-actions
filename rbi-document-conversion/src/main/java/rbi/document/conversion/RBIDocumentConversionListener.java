package rbi.document.conversion;

import com.bcl.easyconverter.html.PDF2HTML;
import com.bcl.easyconverter.html.PDF2HTMLException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(immediate = true, service = ModelListener.class)
public class RBIDocumentConversionListener extends BaseModelListener<JournalArticle> {

	
	@Override
	public void onAfterUpdate(JournalArticle model) throws ModelListenerException {
		// TODO Auto-generated method stub
		
		System.out.println("RBI Document Conversion");
DDMStructure ddmStructure= ddmStructureLocalService.fetchStructure(model.getGroupId(),classNameLocalService.getClassNameId(JournalArticle.class.getName()),model.getDDMStructureKey());

		System.out.println(ddmStructure.getName(Locale.ENGLISH));
		if(ddmStructure.getName(Locale.ENGLISH).equals("RBI Research Paper"))	
		{
		
			System.out.println("document"+model.getDocument().asXML());
			
		Document document = model.getDocument();
	 
		 if(Validator.isNotNull(document)) {
				 
		Node docNode=  document.selectSingleNode("/root/dynamic-element[@name='UploadResearchDocument']/dynamic-content"); 
									 
		JSONObject jsonObject = null;
		try {
			jsonObject = JSONFactoryUtil.createJSONObject(
			docNode.getText());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println();
		String uuid = jsonObject.getString("uuid");
		long groupId = jsonObject.getLong("groupId");

		try {
			DLFileEntry dlFileEntry=_dDlFileEntryLocalService.getFileEntryByUuidAndGroupId(uuid, groupId);
			System.out.println("FileName "+dlFileEntry.getFileName());
			
	String htmlContent=  getPDFConvertedtoHTML(dlFileEntry, model);
			
			  Node docUpdateNode =document.selectSingleNode("/root/dynamic-element[@name='pdfContent']/dynamic-content");
			     Element htmlElement = (Element)docUpdateNode;
			     
			     htmlElement.clearContent();

			     htmlElement.addCDATA(htmlContent);
		   
			     model.setContent(htmlElement.getDocument().asXML());
			  
			   
			   
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}						
		 }
		}
		super.onAfterUpdate(model);
	}
	
	public String getPDFConvertedtoHTML(DLFileEntry dlFileEntry,JournalArticle journalArticle)
	{
		String htmlText=null;
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
		 System.out.println("HTMLContent"+htmlString);
		 
		 org.jsoup.nodes.Document document=Jsoup.parse(htmlString);
		    
		     htmlText= document.html();
		 System.out.println("After parsing"+htmlText);
		 
		 
		} catch (PDF2HTMLException | PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return htmlText;
	}
	
	@Reference
	private DDMStructureLocalService ddmStructureLocalService;
	 
	@Reference
	ClassNameLocalService classNameLocalService;
	
		@Reference
			private DLFileEntryLocalService _dDlFileEntryLocalService;
}
