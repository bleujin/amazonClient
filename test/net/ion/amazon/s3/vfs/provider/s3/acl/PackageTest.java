package net.ion.amazon.s3.vfs.provider.s3.acl;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.ion.amazon.AmzClientModule;
import net.ion.amazon.common.Credential;
import net.ion.amazon.s3.vfs.acl.Acl;
import net.ion.amazon.s3.vfs.acl.IAclGetter;
import net.ion.amazon.s3.vfs.acl.IAclSetter;
import net.ion.amazon.s3.vfs.provider.S3FileProvider;
import net.ion.amazon.s3.vfs.provider.operations.S3FileOperationsProvider;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.VFS;
import net.ion.framework.vfs.VFile;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.operations.FileOperationProvider;
import org.apache.commons.vfs2.provider.FileProvider;


public class PackageTest extends TestCase{

    private FileSystemEntry fsEntry;

    private VFile file;

    private Acl fileAcl;

    private String bucketName;

//    @BeforeClass
    public void setUp () throws FileNotFoundException, IOException {
        bucketName = "vfs-test-bucket";

        fsEntry = VFS.createEmpty() ;
        fsEntry.addProvider("s3", AmzClientModule.testInjector().getInstance(FileProvider.class)) ;
        fsEntry.addOperationProvider("s3", AmzClientModule.testInjector().getInstance(FileOperationProvider.class)) ;
        file = fsEntry.resolveFile("s3://" + bucketName + "/test-place/backup.zip");
    }

//  @AfterClass
    public void restoreAcl () throws FileSystemException {
        fileAcl.denyAll();
        fileAcl.allow(Acl.Group.OWNER);

        // Set ACL
        IAclSetter aclSetter = (IAclSetter)file.getFileOperations().getOperation(IAclSetter.class);
        aclSetter.setAcl(fileAcl);
        aclSetter.process();
    }
    
    public void testAcl () throws FileSystemException {
        // Get ACL
        IAclGetter aclGetter = (IAclGetter)file.getFileOperations().getOperation(IAclGetter.class);
        aclGetter.process();
        fileAcl = aclGetter.getAcl();

        // Owner can read/write
        Assert.assertTrue(aclGetter.canRead(Acl.Group.OWNER));
        Assert.assertTrue(aclGetter.canWrite(Acl.Group.OWNER));

        // Authorized coldn't read/write
        Assert.assertFalse(aclGetter.canRead(Acl.Group.AUTHORIZED));
        Assert.assertFalse(aclGetter.canWrite(Acl.Group.AUTHORIZED));

        // Guest also coldn't read/write
        Assert.assertFalse(aclGetter.canRead(Acl.Group.EVERYONE));
        Assert.assertFalse(aclGetter.canWrite(Acl.Group.EVERYONE));
    }

//    @Test(dependsOnMethods="getAcl")
    public void setAcl () throws FileSystemException {
        // Set allow read to Guest
        fileAcl.allow(Acl.Group.EVERYONE, Acl.Permission.READ);
        IAclSetter aclSetter = (IAclSetter)file.getFileOperations().getOperation(IAclSetter.class);
        aclSetter.setAcl(fileAcl);
        aclSetter.process();

        // Verify
        IAclGetter aclGetter = (IAclGetter)file.getFileOperations().getOperation(IAclGetter.class);
        aclGetter.process();
        Acl changedAcl = aclGetter.getAcl();

        // Guest can read
        Assert.assertTrue(changedAcl.isAllowed(Acl.Group.EVERYONE, Acl.Permission.READ));
        // Write rules for guest not changed
        Assert.assertEquals(
            changedAcl.isAllowed(Acl.Group.EVERYONE, Acl.Permission.WRITE),
            fileAcl.isAllowed(Acl.Group.EVERYONE, Acl.Permission.WRITE)
        );
        // Read rules not spreaded to another groups
        Assert.assertEquals(
            changedAcl.isAllowed(Acl.Group.AUTHORIZED, Acl.Permission.READ),
            fileAcl.isAllowed(Acl.Group.AUTHORIZED, Acl.Permission.READ)
        );
        Assert.assertEquals(
            changedAcl.isAllowed(Acl.Group.OWNER, Acl.Permission.READ),
            fileAcl.isAllowed(Acl.Group.OWNER, Acl.Permission.READ)
        );

        fileAcl = changedAcl;
    }

//    @Test(dependsOnMethods="setAcl")
    public void setAcl2 () throws FileSystemException {
        // Set allow all to Authorized
        fileAcl.allow(Acl.Group.AUTHORIZED);
        IAclSetter aclSetter = (IAclSetter)file.getFileOperations().getOperation(IAclSetter.class);
        aclSetter.setAcl(fileAcl);
        aclSetter.process();

        // Verify
        IAclGetter aclGetter = (IAclGetter)file.getFileOperations().getOperation(IAclGetter.class);
        aclGetter.process();
        Acl changedAcl = aclGetter.getAcl();

        // Authorized can do everything
        Assert.assertTrue(changedAcl.isAllowed(Acl.Group.AUTHORIZED, Acl.Permission.READ));
        Assert.assertTrue(changedAcl.isAllowed(Acl.Group.AUTHORIZED, Acl.Permission.WRITE));

        // All other rules not changed
        Assert.assertEquals(
            changedAcl.isAllowed(Acl.Group.EVERYONE, Acl.Permission.READ),
            fileAcl.isAllowed(Acl.Group.EVERYONE, Acl.Permission.READ)
        );
        Assert.assertEquals(
            changedAcl.isAllowed(Acl.Group.EVERYONE, Acl.Permission.WRITE),
            fileAcl.isAllowed(Acl.Group.EVERYONE, Acl.Permission.WRITE)
        );
        Assert.assertEquals(
            changedAcl.isAllowed(Acl.Group.OWNER, Acl.Permission.READ),
            fileAcl.isAllowed(Acl.Group.OWNER, Acl.Permission.READ)
        );
        Assert.assertEquals(
            changedAcl.isAllowed(Acl.Group.OWNER, Acl.Permission.WRITE),
            fileAcl.isAllowed(Acl.Group.OWNER, Acl.Permission.WRITE)
        );

        fileAcl = changedAcl;
    }

//    @Test(dependsOnMethods={"setAcl2"})
    public void setAcl3 () throws FileSystemException {
        // Set deny to all
        fileAcl.denyAll();
        IAclSetter aclSetter = (IAclSetter)file.getFileOperations().getOperation(IAclSetter.class);
        aclSetter.setAcl(fileAcl);
        aclSetter.process();

        // Verify
        IAclGetter aclGetter = (IAclGetter)file.getFileOperations().getOperation(IAclGetter.class);
        aclGetter.process();
        Acl changedAcl = aclGetter.getAcl();

        Assert.assertTrue(changedAcl.isDenied(Acl.Group.OWNER, Acl.Permission.READ));
        Assert.assertTrue(changedAcl.isDenied(Acl.Group.OWNER, Acl.Permission.WRITE));
        Assert.assertTrue(changedAcl.isDenied(Acl.Group.AUTHORIZED, Acl.Permission.READ));
        Assert.assertTrue(changedAcl.isDenied(Acl.Group.AUTHORIZED, Acl.Permission.WRITE));
        Assert.assertTrue(changedAcl.isDenied(Acl.Group.EVERYONE, Acl.Permission.READ));
        Assert.assertTrue(changedAcl.isDenied(Acl.Group.EVERYONE, Acl.Permission.WRITE));
    }


}
