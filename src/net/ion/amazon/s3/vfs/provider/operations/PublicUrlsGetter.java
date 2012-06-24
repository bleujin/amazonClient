package net.ion.amazon.s3.vfs.provider.operations;

import net.ion.amazon.s3.vfs.acl.IPublicUrlsGetter;
import net.ion.amazon.s3.vfs.provider.S3FileObject;

import org.apache.commons.vfs2.FileSystemException;

/**
 * @author <A href="mailto:alexey at abashev dot ru">Alexey Abashev</A>
 * @version $Id: PublicUrlsGetter.java,v 1.1 2012/04/19 02:14:42 bleujin Exp $
 */
class PublicUrlsGetter implements IPublicUrlsGetter {
	private final S3FileObject file;

	/**
	 * @param file
	 */
	public PublicUrlsGetter(S3FileObject file) {
		this.file = file;
	}

	public String getHttpUrl() {
		return file.getHttpUrl();
	}

	public String getPrivateUrl() {
		return file.getPrivateUrl();
	}

	public String getSignedUrl(int expireInSeconds) throws FileSystemException {
		return file.getSignedUrl(expireInSeconds);
	}

	public void process() throws FileSystemException {
		// Nothing to do
	}
}
