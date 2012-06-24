package net.ion.amazon.s3.vfs.provider.operations;

import net.ion.amazon.s3.vfs.acl.IMD5HashGetter;
import net.ion.amazon.s3.vfs.provider.S3FileObject;

import org.apache.commons.vfs2.FileSystemException;

/**
 * @author <A href="mailto:alexey at abashev dot ru">Alexey Abashev</A>
 * @version $Id: MD5HashGetter.java,v 1.1 2012/04/19 02:14:42 bleujin Exp $
 */
public class MD5HashGetter implements IMD5HashGetter {
	private final S3FileObject file;

	/**
	 * @param file
	 */
	public MD5HashGetter(S3FileObject file) {
		this.file = file;
	}

	public String getMD5Hash() throws FileSystemException {
		return file.getMD5Hash();
	}

	public void process() throws FileSystemException {
		// Do nothing
	}
}
