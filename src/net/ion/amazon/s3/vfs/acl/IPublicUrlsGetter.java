package net.ion.amazon.s3.vfs.acl;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.operations.FileOperation;

public interface IPublicUrlsGetter extends FileOperation {

	String getHttpUrl(); //Get direct http url to file.
	String getPrivateUrl(); //Get private url in format s3://awsKey:awsSecretKey/bucket-name/object-name
	String getSignedUrl(int expireInSeconds) throws FileSystemException;
}
