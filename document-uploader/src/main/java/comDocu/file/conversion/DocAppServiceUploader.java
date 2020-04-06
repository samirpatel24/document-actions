package comDocu.file.conversion;

import com.liferay.document.library.kernel.service.DLAppLocalServiceWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;

import java.io.File;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;



@Component(
		immediate = true, 
		property = {
		},
		service = ServiceWrapper.class
	)
public class DocAppServiceUploader extends DLAppLocalServiceWrapper
{

	public DocAppServiceUploader() {
		super(null);
	}
		
	
	 @Override
	public FileEntry addFileEntry(long userId, long repositoryId, long folderId, String sourceFileName, String mimeType,
			byte[] bytes, ServiceContext serviceContext) throws PortalException {
		// TODO Auto-generated method stub
		 
		 System.out.println("DLAppLocal Service addFileEntry-1 and file is "+sourceFileName);
		return super.addFileEntry(userId, repositoryId, folderId, sourceFileName, mimeType, bytes, serviceContext);
	}

	@Override
	public FileEntry addFileEntry(long userId, long repositoryId, long folderId, String sourceFileName, String mimeType,
			String title, String description, String changeLog, byte[] bytes, ServiceContext serviceContext)
			throws PortalException {
		// TODO Auto-generated method stub
		
		 System.out.println("DLAppLocal Service addFileEntry-2 and file is "+sourceFileName);

		return super.addFileEntry(userId, repositoryId, folderId, sourceFileName, mimeType, title, description, changeLog,
				bytes, serviceContext);
	}
	
	@Override
	public FileEntry addFileEntry(long userId, long repositoryId, long folderId, String sourceFileName, String mimeType,
			String title, String description, String changeLog, File file, ServiceContext serviceContext)
			throws PortalException {
		// TODO Auto-generated method stub
		 System.out.println("DLAppLocal Service addFileEntry-3 and file is "+sourceFileName);

		return super.addFileEntry(userId, repositoryId, folderId, sourceFileName, mimeType, title, description, changeLog, file,
				serviceContext);
	}
	
	@Override
	public FileEntry addFileEntry(long userId, long repositoryId, long folderId, String sourceFileName, String mimeType,
			String title, String description, String changeLog, InputStream is, long size,
			ServiceContext serviceContext) throws PortalException {
		// TODO Auto-generated method stub
		 System.out.println("DLAppLocal Service -4 and file is "+sourceFileName);

		return super.addFileEntry(userId, repositoryId, folderId, sourceFileName, mimeType, title, description, changeLog, is,
				size, serviceContext);
	}
	
	@Override
	public void updateAsset(long userId, FileEntry fileEntry, FileVersion fileVersion, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds) throws PortalException {
		// TODO Auto-generated method stub
		
		System.out.println("Inside Asset :"+fileEntry.getFileName());
		super.updateAsset(userId, fileEntry, fileVersion, assetCategoryIds, assetTagNames, assetLinkEntryIds);
	}
	
	
  @Override
public FileEntry updateFileEntry(long userId, long fileEntryId, String sourceFileName, String mimeType,
		String title, String description, String changeLog, boolean majorVersion, byte[] bytes,
		ServiceContext serviceContext) throws PortalException {
	// TODO Auto-generated method stub
	  
		System.out.println("Inside updateFileEntry 1 :");

	return super.updateFileEntry(userId, fileEntryId, sourceFileName, mimeType, title, description, changeLog, majorVersion,
			bytes, serviceContext);
}
  @Override
public FileEntry updateFileEntry(long userId, long fileEntryId, String sourceFileName, String mimeType,
		String title, String description, String changeLog, boolean majorVersion, File file,
		ServiceContext serviceContext) throws PortalException {
	// TODO Auto-generated method stub
	  
		System.out.println("Inside updateFileEntry 2:");

	return super.updateFileEntry(userId, fileEntryId, sourceFileName, mimeType, title, description, changeLog, majorVersion,
			file, serviceContext);
}
	

}
