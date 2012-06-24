package net.ion.amazon.s3.vfs.provider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Singleton;

import net.ion.amazon.common.Credential;
import net.ion.framework.util.ObjectUtil;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticationData;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.util.UserAuthenticatorUtils;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;

import com.google.inject.Inject;


public class S3FileProvider extends AbstractOriginatingFileProvider {

	public final static Collection<Capability> capabilities = Collections.unmodifiableCollection(
			Arrays.asList(
						Capability.CREATE, 
						Capability.DELETE, 
						Capability.RENAME, 
						Capability.GET_TYPE, 
						Capability.GET_LAST_MODIFIED, 
						Capability.SET_LAST_MODIFIED_FILE, 
						Capability.SET_LAST_MODIFIED_FOLDER, 
						Capability.LIST_CHILDREN, 
						Capability.READ_CONTENT, 
						Capability.URI, 
						Capability.WRITE_CONTENT
						// Capability.APPEND_CONTENT
			));
	private final static UserAuthenticationData.Type[] AUTHENTICATOR_TYPES = new UserAuthenticationData.Type[] { UserAuthenticationData.USERNAME, UserAuthenticationData.PASSWORD };
	private static FileSystemOptions defaultOptions = new FileSystemOptions();

	private Credential credential ;
	
	@Inject @Singleton
	public S3FileProvider(Credential credential){
		super() ;
		this.credential = credential ;
		setFileNameParser(S3FileNameParser.getInstance());
	}
	

	@Override
	protected FileSystem doCreateFileSystem(FileName fileName, FileSystemOptions fileSystemOptions) throws FileSystemException {
		FileSystemOptions fsOptions = ObjectUtil.coalesce(fileSystemOptions, defaultOptions);
		
		Credential cre = makeCredential(fsOptions) ; 
		S3Service service = initService(cre) ;

		return new S3FileSystem((S3FileName) fileName, service, fsOptions);
	}

	
	private Credential makeCredential(FileSystemOptions fsOptions) {
		if (credential != null) return credential ;
		
		UserAuthenticationData authData = UserAuthenticatorUtils.authenticate(fsOptions, AUTHENTICATOR_TYPES);

		// Fetch AWS key-id and secret key from authData
		String accessKey = UserAuthenticatorUtils.toString(UserAuthenticatorUtils.getData(authData, UserAuthenticationData.USERNAME, null));
		String secretKey = UserAuthenticatorUtils.toString(UserAuthenticatorUtils.getData(authData, UserAuthenticationData.PASSWORD, null));

		UserAuthenticatorUtils.cleanup(authData);		
		return Credential.create(accessKey, secretKey) ;
	}


	private S3Service initService(Credential myCredential) throws FileSystemException{
		try {
			RestS3Service service = new RestS3Service(myCredential.toJet3Credential());
			return service ;
		} catch (S3ServiceException e) {
			throw new FileSystemException(e) ;
		} 
	}


	public Collection<Capability> getCapabilities() {
		return capabilities;
	}
}
