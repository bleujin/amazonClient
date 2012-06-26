package net.ion.amazon.s3.vfs.provider.s3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.ion.amazon.AmzClientModule;
import net.ion.amazon.s3.vfs.provider.S3FileProvider;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.VFS;
import net.ion.framework.vfs.VFile;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.provider.url.UrlFileProvider;

public class TestS3Provider extends TestCase {

	private FileSystemEntry fsEntry;

	private String fileName, dirName, bucketName, bigFile;

	// @BeforeClass
	public void setUp() throws FileNotFoundException, IOException {
		fsEntry = VFS.createEmpty();
		fsEntry.addProvider("s3", AmzClientModule.testInjector().getInstance(S3FileProvider.class));
		fsEntry.addProvider("file", new UrlFileProvider());

		Random r = new Random();
		fileName = "vfs-file" + r.nextInt(1000);
		dirName = "vfs-dir" + r.nextInt(1000);

		bucketName = "vfs-test-bucket";
		bigFile = "resource/backup.zip";
	}

	public void testCreateFileOk() throws FileSystemException {
		VFile file = fsEntry.resolveFile("s3://" + bucketName + "/test-place/" + fileName);
		file.createFile();
		Assert.assertTrue(file.exists());
	}

	public void testCreateFileFailed() throws FileSystemException {
		try {
			VFile tmpFile = fsEntry.resolveFile("s3://../new-mpoint/vfs-bad-file");
			tmpFile.createFile();
			fail();
		} catch (FileSystemException expect) {
		}
	}

	public void testCreateFileFailed2() throws Exception {
		VFile tmpFile = fsEntry.resolveFile("s3://" + bucketName + "/test-place/" + fileName);
		tmpFile.createFile();
		tmpFile.write(new ByteArrayInputStream(new byte[] { 65, 66 }));
		tmpFile.close();
		Assert.assertTrue(tmpFile.exists());
		try {
			VFile dupFIle = fsEntry.resolveFile("s3://" + bucketName + "/test-place/" + fileName);
			dupFIle.createFile();
			fail();
		} catch (FileSystemException expect) {
		}
	}

	public void teteCreateDirOk() throws FileSystemException {
		VFile dir = fsEntry.resolveFile("s3://" + bucketName + "/test-place/" + dirName);
		dir.createFolder();
		Assert.assertTrue(dir.exists());
	}

	public void testCreateDirFailed() throws FileSystemException {
		VFile tmpFile = fsEntry.resolveFile("s3://../new-mpoint/vfs-bad-dir");
		try {
			tmpFile.createFolder();
		} catch (FileSystemException expect) {
		}
	}

	public void testUpload() throws FileNotFoundException, IOException {
		VFile dest = fsEntry.resolveFile("s3://" + bucketName + "/test-place/backup.zip");
		if (dest.exists()) {
			dest.delete();
		}

		final File srcFile = new File(bigFile);
		Assert.assertTrue("Backup file should exists", srcFile.exists());

		VFile src = fsEntry.resolveFile(srcFile.toURI().toString());
		dest.copyFrom(src, Selectors.SELECT_SELF);
		Assert.assertTrue(dest.exists() && dest.getType().equals(FileType.FILE));
	}

	public void testDownload() throws IOException {
		VFile typica = fsEntry.resolveFile("s3://" + bucketName + "/test-place/backup.zip");
		File localCache = File.createTempFile("vfs.", ".s3-test");

		// Copy from S3 to localfs
		FileOutputStream out = new FileOutputStream(localCache);
		IOUtils.copy(typica.getContent().getInputStream(), out);

		// Test that file sizes equals
		Assert.assertEquals(localCache.length(), typica.getContent().getSize());
		localCache.delete();
	}

	public void findFiles() throws FileSystemException {
		VFile baseDir = fsEntry.resolveFile("s3://" + bucketName + "/test-place/find-test");
		baseDir.createFolder();

		// Create files and dirs
		fsEntry.resolveFile(baseDir, "child-file.tmp").createFile();
		fsEntry.resolveFile(baseDir, "child-file2.tmp").createFile();
		fsEntry.resolveFile(baseDir, "child-dir").createFolder();
		fsEntry.resolveFile(baseDir, "child-dir/descendant.tmp").createFile();
		fsEntry.resolveFile(baseDir, "child-dir/descendant2.tmp").createFile();
		fsEntry.resolveFile(baseDir, "child-dir/descendant-dir").createFolder();

		Assert.assertEquals(3, baseDir.findFiles(Selectors.SELECT_CHILDREN).length);
		Assert.assertEquals(3, baseDir.findFiles(Selectors.SELECT_FOLDERS).length);
		Assert.assertEquals(4, baseDir.findFiles(Selectors.SELECT_FILES).length);
		Assert.assertEquals(6, baseDir.findFiles(Selectors.EXCLUDE_SELF).length);
	}

	public void testDelete() throws FileSystemException {
		VFile dir = fsEntry.resolveFile("s3://" + bucketName + "/test-place");
		VFile testsDir = fsEntry.resolveFile(dir, "find-tests");
		testsDir.delete(Selectors.EXCLUDE_SELF);

		// Only tests dir must remains
//		VFile[] files = dir.findFiles(Selectors.SELECT_ALL);
//		Assert.assertEquals(1, files.length);
	}

}
//
//
// /**
// * Create file on already existed folder
// * @throws FileSystemException
// */
// //@Test(expectedExceptions={FileSystemException.class}, dependsOnMethods={"createDirOk"})
// public void createDirFailed2 () throws FileSystemException {
// VFile tmpFile = fsManager.resolveFile("s3://" + bucketName + "/test-place/" + dirName);
// tmpFile.createFile();
// }
//
// //@Test(dependsOnMethods={"upload"})
// public void exists () throws FileNotFoundException, IOException {
// // Existed dir
// VFile existedDir = fsManager.resolveFile("s3://" + bucketName + "/test-place");
// Assert.assertTrue(existedDir.exists());
//
// // Non-existed dir
// VFile nonExistedDir = fsManager.resolveFile(existedDir, "path/to/non/existed/dir");
// Assert.assertFalse(nonExistedDir.exists());
//
// // Existed file
// VFile existedFile = fsManager.resolveFile("s3://" + bucketName + "/test-place/backup.zip");
// Assert.assertTrue(existedFile.exists());
//
// // Non-existed file
// VFile nonExistedFile = fsManager.resolveFile("s3://" + bucketName + "/ne/b?lo/i/net");
// Assert.assertFalse(nonExistedFile.exists());
// }
//
// //@Test(dependsOnMethods={"createFileOk"})

//
// //@Test(dependsOnMethods={"createFileOk"})
// public void uploadMultiple() throws Exception {
// VFile dest = fsManager.resolveFile("s3://" + bucketName + "/test-place/backup.zip");
//
// // Delete file if exists
// if (dest.exists()) {
// dest.delete();
// }
//
// // Copy data
// final File backupFile = new File(BACKUP_ZIP);
//
// Assert.assertTrue("Backup file should exists", backupFile.exists());
//
// VFile src = fsManager.resolveFile(backupFile.getAbsolutePath());
//
// // copy twice
// dest.copyFrom(src, Selectors.SELECT_SELF);
// Thread.sleep(2000L);
// dest.copyFrom(src, Selectors.SELECT_SELF);
//
// Assert.assertTrue(dest.exists() && dest.getType().equals(FileType.FILE));
// }
//
// //@Test(dependsOnMethods={"createFileOk"})
// public void uploadBigFile() throws FileNotFoundException, IOException {
// VFile dest = fsManager.resolveFile("s3://" + bucketName + "/big_file.iso");
//
// // Delete file if exists
// if (dest.exists()) {
// dest.delete();
// }
//
// // Copy data
// final File file = new File(bigFile);
//
// Assert.assertTrue("Backup file should exists", file.exists());
//
// VFile src = fsManager.resolveFile(file.getAbsolutePath());
//
// dest.copyFrom(src, Selectors.SELECT_SELF);
//
// Assert.assertTrue(dest.exists() && dest.getType().equals(FileType.FILE));
// }
//

//
// //@Test(dependsOnMethods={"createFileOk", "createDirOk"})
// public void listChildren () throws FileSystemException {
// VFile baseDir = fsManager.resolveFile(dir, "list-children-test");
// baseDir.createFolder();
//
// for (int i=0; i<5; i++) {
// FileObject tmpFile = fsManager.resolveFile(baseDir, i + ".tmp");
// tmpFile.createFile();
// }
//
// FileObject[] children = baseDir.getChildren();
// Assert.assertEquals(children.length, 5);
// }
//

//
// //@Test(dependsOnMethods={"createFileOk", "createDirOk"})
// public void getType () throws FileSystemException {
// VFile imagine = fsManager.resolveFile(dir, "imagine-there-is-no-countries");
// Assert.assertEquals(imagine.getType(), FileType.IMAGINARY);
// Assert.assertEquals(dir.getType(), FileType.FOLDER);
// Assert.assertEquals(file.getType(), FileType.FILE);
// }
//
// //@Test(dependsOnMethods={"upload"})
// public void getContentType () throws FileSystemException {
// VFile backup = fsManager.resolveFile("s3://" + bucketName + "/test-place/backup.zip");
// Assert.assertEquals(backup.getContent().getContentInfo().getContentType(), "application/zip");
// }
//
// //@Test(dependsOnMethods={"upload"})
// public void getSize () throws FileSystemException {
// VFile backup = fsManager.resolveFile("s3://" + bucketName + "/test-place/backup.zip");
// Assert.assertEquals(backup.getContent().getSize(), 996166);
// }
//
// //@Test(dependsOnMethods={"upload"})
// public void getUrls() throws FileSystemException {
// VFile backup = fsManager.resolveFile("s3://" + bucketName + "/test-place/backup.zip");
//
// Assert.assertTrue(backup.getFileOperations().hasOperation(IPublicUrlsGetter.class));
//
// IPublicUrlsGetter urlsGetter = (IPublicUrlsGetter) backup.getFileOperations().getOperation(IPublicUrlsGetter.class);
//
// Assert.assertEquals(urlsGetter.getHttpUrl(), "http://" + bucketName + ".s3.amazonaws.com/test-place/backup.zip");
// Assert.assertTrue(urlsGetter.getPrivateUrl().endsWith("@" + bucketName + "/test-place/backup.zip"));
// Assert.assertTrue(urlsGetter.getSignedUrl(60).startsWith("https://" + bucketName + ".s3.amazonaws.com/test-place/backup.zip?AWSAccessKeyId="));
// }
//
// //@Test(dependsOnMethods={"upload"})
// public void getMD5Hash() throws NoSuchAlgorithmException, FileNotFoundException, IOException {
// VFile backup = fsManager.resolveFile("s3://" + bucketName + "/test-place/backup.zip");
//
// Assert.assertTrue(backup.getFileOperations().hasOperation(IMD5HashGetter.class));
//
// IMD5HashGetter md5Getter = (IMD5HashGetter) backup.getFileOperations().getOperation(IMD5HashGetter.class);
//
// String md5Remote = md5Getter.getMD5Hash();
//
// Assert.assertNotNull(md5Remote);
//
// final File backupFile = new File(BACKUP_ZIP);
//
// Assert.assertTrue("Backup file should exists", backupFile.exists());
//
// String md5Local = toHex(computeMD5Hash(new FileInputStream(backupFile)));
//
// Assert.assertEquals(md5Remote, md5Local, "Local and remote md5 should be equal");
// }
//

// //@AfterClass
// public void tearDown () throws FileSystemException {
// try {
// VFile vfsTestDir = fsManager.resolveFile(dir, "..");
// vfsTestDir.delete(Selectors.SELECT_ALL);
// } catch (Exception e) {
// }
// }

