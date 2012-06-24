package net.ion.amazon.s3.vfs.provider.s3;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
import net.ion.amazon.AmzClientModule;
import net.ion.amazon.common.Credential;
import net.ion.amazon.s3.vfs.S3Shell;
import net.ion.amazon.s3.vfs.provider.S3FileProvider;
import net.ion.amazon.s3.vfs.provider.operations.S3FileOperationsProvider;
import net.ion.framework.util.Debug;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.VFS;
import net.ion.framework.vfs.VFile;

import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.operations.FileOperationProvider;
import org.apache.commons.vfs2.provider.FileProvider;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestProvider extends TestCase {
	
	private Injector injector ;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		injector = AmzClientModule.testInjector() ;
		
	}
	
	public void testListFile() throws Exception {
		FileSystemEntry entry = VFS.createEmpty() ;
		entry.addProvider("s3", injector.getInstance(FileProvider.class)) ;
		
		entry.addOperationProvider("s3", injector.getInstance(FileOperationProvider.class)) ;
		
		VFile vf = entry.resolveFile("s3://bleujin") ;
		Debug.line(vf.getChildren()) ;
	}

	
	public void testLs() throws Exception {
		S3Shell s = injector.getInstance(S3Shell.class) ;
		VFile[] files = s.ls("s3://bleujin") ;
		
		if (files != null && files.length > 0) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (VFile fo : files) {
				if (FileType.FILE.equals(fo.getType())) {
					String lastModDate = df.format(new Date(fo.getContent().getLastModifiedTime()));
					long size = fo.getContent().getSize();
					Debug.debug(String.format("%-20s%-10s%s", lastModDate, size, fo.getName().getURI()));
				} else {
					Debug.debug(String.format("%-20s%-10s%s", null, 0, fo.getName().getURI()));
				}
			}
		}
	}
	
}
