package com.liferay.rbi.service;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.FieldsToDDMFormValuesConverter;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.util.JournalConverter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ExpandedTitleContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.jsoup.Jsoup;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

@Component(immediate = true, service = ModelListener.class)
public class JournalConversionModelListener extends BaseModelListener<JournalArticle>{
	
	
	private static Log logger = LogFactoryUtil.getLog(JournalConversionModelListener.class);


	@Override
	public void onAfterUpdate(JournalArticle model) throws ModelListenerException {
		// TODO Auto-generated method stub
		try
		{	
 DDMStructure ddmStructure= ddmStructureLocalService.fetchStructure(model.getGroupId(),classNameLocalService.getClassNameId(JournalArticle.class.getName()),model.getDDMStructureKey());
			if(ddmStructure.getName().equals("RBI Research Paper"))
			
			{
			
				logger.debug("document"+model.getDocument().asXML());
				
			Document document = model.getDocument();
		 
			 if(Validator.isNotNull(document)) {
					 
			Node docNode=  document.selectSingleNode("/root/dynamic-element[@name='UploadResearchDocument']/dynamic-content"); 
										 
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			docNode.getText());
			//System.out.println();
			String uuid = jsonObject.getString("uuid");
			long groupId = jsonObject.getLong("groupId");

			DLFileEntry dlFileEntry=_dDlFileEntryLocalService.getFileEntryByUuidAndGroupId(uuid, groupId);
														
			//String htmlContent= getDocumentConvertedtoHTML(model.getGroupId(), dlFileEntry);
			  String htmlContent = convertPDFtoHTML(dlFileEntry);
		 	
				 Node docUpdateNode =document.selectSingleNode("/root/dynamic-element[@name='pdfContent']/dynamic-content");
			     Element htmlElement = (Element)docUpdateNode;
			     
			     htmlElement.clearContent();

			     htmlElement.addCDATA(htmlContent);
		   
			     model.setContent(htmlElement.getDocument().asXML());
			
					 }	 
				} 
				
				  }catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (PortalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (TikaException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SAXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					 
					

		
		super.onAfterUpdate(model);
	}
	
	
	 public String convertPDFtoHTML(DLFileEntry dlFileEntry) throws PortalException, IOException, TikaException, SAXException
	 {
		 
		 AutoDetectParser autoDetectParser= new AutoDetectParser();

		BodyContentHandler handler =new BodyContentHandler();
		
		Metadata metadata = new Metadata();
		
	    autoDetectParser.parse(dlFileEntry.getContentStream(), handler, metadata);
		 
		 return handler.toString();
		 
	 }
	
	 
	 /*private String getDocumentConvertedtoHTML( long groupId, DLFileEntry dlFileEntry) {
		// TODO Auto-generated method stub
		 
		 	String htmlText=null;
		 long userId =PrincipalThreadLocal.getUserId();
		 
		try {
			User user= UserLocalServiceUtil.getUser(userId);
		//final String  folderPath= (String)user.getExpandoBridge().getAttribute("FolderPath");
			//final String  folderPath= "http://localhost:8080/webdav/guest/document_library/PDFImages";
			final String  folderPath="C:/PDFImages";
					
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    
		    AutoDetectParser tikaParser = new AutoDetectParser();
		    
		    PDFParser parser = new PDFParser(); 
			Metadata metadata= new Metadata();
		    ContentHandler handler =   new ToXMLContentHandler();
		    ParseContext context = new ParseContext();
		   
		    
		    PDFParserConfig config = new PDFParserConfig();
		    config.setExtractInlineImages(true);
		    config.setExtractUniqueInlineImagesOnly(true);
		    parser.setPDFParserConfig(config);

		    SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		    TransformerHandler handler;
			
				handler = factory.newTransformerHandler();
			
		    handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
		    handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
		    handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    handler.setResult(new StreamResult(out));
		    ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);
		    
		    EmbeddedDocumentExtractor embeddedDocumentExtractor = 
		            new EmbeddedDocumentExtractor() {
		        @Override
		        public boolean shouldParseEmbedded(Metadata metadata) {
		            return true;
		        }
		        @Override
		        public void parseEmbedded(InputStream stream, ContentHandler handler, Metadata metadata, boolean outputHtml)
		                throws SAXException, IOException {
		        	Path outputDir = new File(folderPath + "_").toPath();
		            Files.createDirectories(outputDir);

		            Path outputPath = new File(outputDir.toString() + "/" + metadata.get(Metadata.RESOURCE_NAME_KEY)).toPath();
		            Files.deleteIfExists(outputPath);
		            Files.copy(stream, outputPath);
		        }
		    };
		    context.set(AutoDetectParser.class, tikaParser);
		    context.set(EmbeddedDocumentExtractor.class,embeddedDocumentExtractor );
		    
		    
		   // tikaParser.parse(dlFileEntry.getContentStream(), handler1, new Metadata());
		    tikaParser.parse(dlFileEntry.getContentStream(), handler, metadata, context);
		    
		    org.jsoup.nodes.Document document=Jsoup.parseBodyFragment(new String(handler.toString()));
		    document.outputSettings().prettyPrint(false);
		     htmlText= document.body().html();
		  
		     System.out.println(htmlText);
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
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		    
		return htmlText;
	}
*/
	 
	 
	
	@Reference
	private JournalConverter journalConverter;
	
	@Reference
	private FieldsToDDMFormValuesConverter _fieldsToDDMFormValuesConverter;

	@Reference
	private DLFileEntryLocalService _dDlFileEntryLocalService;
	
	@Reference
	private DDMStructureLocalService ddmStructureLocalService;
	
	@Reference
	ClassNameLocalService classNameLocalService;
	
	@Reference
	private DLFolderLocalService dlFolderLocalService;
	
	
	@Reference
	Portal _portal;
	
	

}

