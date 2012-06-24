package net.ion.amazon.s3.vfs.acl;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.operations.FileOperation;

/**
 * Interface for setting file Access Control List.
 * 
 * @author Marat Komarov
 */
public interface IAclSetter extends FileOperation {

	void setAcl(Acl acl); //Sets file Access Control List.

	void process() throws FileSystemException; //Executes setter operations. Must be called after setAcl.
}
