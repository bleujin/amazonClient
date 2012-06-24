package net.ion.amazon.s3.vfs.provider.operations;

import net.ion.amazon.s3.vfs.acl.Acl;
import net.ion.amazon.s3.vfs.acl.IAclGetter;
import net.ion.amazon.s3.vfs.acl.Acl.Group;
import net.ion.amazon.s3.vfs.provider.S3FileObject;

import org.apache.commons.vfs2.FileSystemException;

class AclGetter implements IAclGetter {

	private S3FileObject file;

	private Acl acl;

	public AclGetter(S3FileObject file) {
		this.file = file;
	}

	public boolean canRead(Group group) {
		return acl.isAllowed(group, Acl.Permission.READ);
	}

	public boolean canWrite(Group group) {
		return acl.isAllowed(group, Acl.Permission.WRITE);
	}

	public Acl getAcl() {
		return acl;
	}

	public void process() throws FileSystemException {
		acl = file.getAcl();
	}

}
