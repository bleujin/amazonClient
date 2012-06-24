package net.ion.amazon.s3.vfs;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.ion.amazon.common.Credential;
import net.ion.amazon.s3.vfs.provider.S3FileProvider;
import net.ion.framework.util.Debug;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.VFS;
import net.ion.framework.vfs.VFile;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;

import com.google.inject.Inject;

public class S3Shell {

	private FileSystemEntry fsEntry;

	@Inject
	public S3Shell(Credential cre) throws FileNotFoundException, IOException {
		fsEntry = VFS.createEmpty();
		fsEntry.addProvider("s3", new S3FileProvider(cre));
	}

	public VFile[] ls(String path) throws FileSystemException {
		VFile file = fsEntry.resolveFile(path);
		Debug.line(file.getChildren());
		return file.findFiles(Selectors.SELECT_ALL);
	}
}
