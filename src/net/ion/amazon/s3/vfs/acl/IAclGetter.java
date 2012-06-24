package net.ion.amazon.s3.vfs.acl;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.operations.FileOperation;


public interface IAclGetter extends FileOperation {

	boolean canRead(Acl.Group group);
	boolean canWrite(Acl.Group group);
	Acl getAcl();

	void process() throws FileSystemException; //Executes getter operation. Must be called before aby other operation methods
}
