package net.ion.amazon.s3.vfs.acl;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.operations.FileOperation;


public interface IMD5HashGetter extends FileOperation {
	String getMD5Hash() throws FileSystemException;
}
