package net.ion.amazon.s3.vfs.provider.operations;

import java.util.Collection;

import net.ion.amazon.s3.vfs.acl.IAclGetter;
import net.ion.amazon.s3.vfs.acl.IAclSetter;
import net.ion.amazon.s3.vfs.acl.IMD5HashGetter;
import net.ion.amazon.s3.vfs.acl.IPublicUrlsGetter;
import net.ion.amazon.s3.vfs.provider.S3FileObject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.operations.FileOperation;
import org.apache.commons.vfs2.operations.FileOperationProvider;

public class S3FileOperationsProvider implements FileOperationProvider {

	@SuppressWarnings( { "rawtypes", "unchecked" })
	public void collectOperations(Collection operationsList, FileObject file) throws FileSystemException {
		if (file instanceof S3FileObject) {
			operationsList.add(AclGetter.class);
			operationsList.add(AclSetter.class);
			operationsList.add(PublicUrlsGetter.class);
			operationsList.add(MD5HashGetter.class);
		}
	}

	@SuppressWarnings("rawtypes")
	public FileOperation getOperation(FileObject file, Class operationClass) throws FileSystemException {
		if (file instanceof S3FileObject) {
			if (operationClass.equals(IAclGetter.class)) {
				// getter
				return new AclGetter((S3FileObject) file);
			} else if (operationClass.equals(IAclSetter.class)) {
				// setter
				return new AclSetter((S3FileObject) file);
			} else if (operationClass.equals(IPublicUrlsGetter.class)) {
				// public urls
				return new PublicUrlsGetter((S3FileObject) file);
			} else if (operationClass.equals(IMD5HashGetter.class)) {
				// get md5 hash
				return new MD5HashGetter((S3FileObject) file);
			}
		}

		throw new FileSystemException(String.format("Operation %s is not provided for file %s", operationClass.getName(), file.getName()));
	}
}
