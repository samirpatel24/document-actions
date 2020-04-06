package com.file.conversion;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceWrapper;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceWrapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ExpandedTitleContentHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.xml.sax.SAXException;

/**
 * @author samir
 */
	@Component(
		immediate = true,
		property = {
		},
		service = ServiceWrapper.class
	)
public class DocumentUploader extends DLFileEntryLocalServiceWrapper {

	public DocumentUploader() {
		super(null);
		
		
	}
	
	@Override
		public DLFileEntry addFileEntry(long userId, long groupId, long repositoryId, long folderId, String sourceFileName,
				String mimeType, String title, String description, String changeLog, long fileEntryTypeId,
				Map<String, DDMFormValues> ddmFormValuesMap, File file, InputStream is, long size,
				ServiceContext serviceContext) throws PortalException {
			// TODO Auto-generated method stub
		
		System.out.println("File Uploaded-Executing AddFileEntry-1");
		
	  AutoDetectParser tikaParser = new AutoDetectParser();

		
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
	    TransformerHandler handler;
		try {
			handler = factory.newTransformerHandler();
		
	    handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
	    handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
	    handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    handler.setResult(new StreamResult(out));
	    ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);

	    tikaParser.parse(is, handler1, new Metadata());
	    System.out.println("Content of the PDF"+handler1.toString());
	    //System.out.println(new String(out.toByteArray(), "UTF-8"));
	    
	    Document document=Jsoup.parseBodyFragment(new String(out.toByteArray()));
	    document.outputSettings().prettyPrint(false);
	    String htmlText= document.body().html();
	    System.out.println("After Remove Body Tag");
	    System.out.println(htmlText);
	    
	    Map<Locale,String> titleMap= new HashMap<Locale, String>();
	    
	    titleMap.put(Locale.ENGLISH, sourceFileName);

	    Map<Locale,String> descriptionMap= new HashMap<Locale, String>();
	    descriptionMap.put(Locale.ENGLISH, "");

	    GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance(TimeZone.getDefault(),Locale.ENGLISH);
  ServiceContext contentSc = new ServiceContext();

	 /*==   JournalArticleLocalServiceUtil.addArticle(userId, groupId, 0, 0, 0, null, true, 0, titleMap,
	    		descriptionMap, htmlText,"BASIC-WEB-CONTENT","BASIC-WEB-CONTENT", null,date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
	    		date.get(Calendar.YEAR), date.get(Calendar.HOUR), date.get(Calendar.MINUTE),0,0, 0, 0, 
	    		0, true,0,0,0,0,0,true,true, false, null, null, null, null, 
	    		contentSc);*/
	 
	    System.out.println("Web COntent Added");
	    
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		

	    /*
	    Document document=Jsoup.parseBodyFragment(new String(out.toByteArray()));
	    document.outputSettings().prettyPrint(false);

	    String htmlText= document.body().html();
	    System.out.println("Inside Body Tag");
	    
	    System.out.println(htmlText);
*/
	  /*  Map<Locale,String> titleMap= new HashMap<Locale, String>();
	    
	    titleMap.put(Locale.ENGLISH, sourceFileName);
 Map<Locale,String> descriptionMap= new HashMap<Locale, String>();
 
 GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance(TimeZone.getDefault(),Locale.ENGLISH);
 
 descriptionMap.put(Locale.ENGLISH, "");
	    titleMap.put(Locale.ENGLISH, sourceFileName);
	    
	     journalArticleLocalService.fetchArticleByUrlTitle(serviceContext.getScopeGroupId(), sourceFileName);
	    
	  if(journalArticleLocalService.fetchArticleByUrlTitle(serviceContext.getScopeGroupId(), sourceFileName).equals(null))
	  {
	    journalArticleLocalService.addArticle
	    (serviceContext.getUserId(),serviceContext.getScopeGroupId(),0, titleMap, descriptionMap, htmlText, "BASIC-WEB-CONTENT","BASIC-WEB-CONTENT",
	    		serviceContext);
	    
	  } */
		/*} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
			return super.addFileEntry(userId, groupId, repositoryId, folderId, sourceFileName, mimeType, title, description,
					changeLog, fileEntryTypeId, ddmFormValuesMap, file, is, size, serviceContext);
		}
	
	
		
@Override
public DLFileEntry addDLFileEntry(DLFileEntry dlFileEntry) {
	// TODO Auto-generated method stub

	
		System.out.println("File Uploaded-Executing AddFileEntry-2");
	return super.addDLFileEntry(dlFileEntry);
}
	


}