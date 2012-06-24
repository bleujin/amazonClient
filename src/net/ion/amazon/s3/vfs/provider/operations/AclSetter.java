package net.ion.amazon.s3.vfs.provider.operations;

import net.ion.amazon.s3.vfs.acl.Acl;
import net.ion.amazon.s3.vfs.acl.IAclSetter;
import net.ion.amazon.s3.vfs.provider.S3FileObject;

import org.apache.commons.vfs2.FileSystemException;

class AclSetter implements IAclSetter {

	private S3FileObject file;

	private Acl acl;

	public AclSetter(S3FileObject file) {
		this.file = file;
	}

	public void setAcl(Acl acl) {
		this.acl = acl;
	}

	public void process() throws FileSystemException {
		file.setAcl(acl);
	}
}
