package net.ion.amazon.s3.vfs.provider.s3.acl;


import junit.framework.Assert;
import junit.framework.TestCase;
import net.ion.amazon.s3.vfs.acl.Acl;


public class TestACL extends TestCase{
	
    public void testOperations () {
        Acl acl = new Acl(null);
        acl.denyAll();

        // Allow single right
        acl.allow(Acl.Group.OWNER, Acl.Permission.WRITE);
        Assert.assertTrue(acl.isAllowed(Acl.Group.OWNER, Acl.Permission.WRITE));

        // Deny single right
        acl.deny(Acl.Group.OWNER, Acl.Permission.WRITE);
        Assert.assertTrue(acl.isDenied(Acl.Group.OWNER, Acl.Permission.WRITE));

        // Allow rights
        Acl.Permission[] rights = {Acl.Permission.READ, Acl.Permission.WRITE};
        acl.allow(Acl.Group.AUTHORIZED, rights);
        Assert.assertTrue(acl.isAllowed(Acl.Group.AUTHORIZED, Acl.Permission.WRITE));
        Assert.assertTrue(acl.isAllowed(Acl.Group.AUTHORIZED, Acl.Permission.READ));

        // Deny rights
        acl.deny(Acl.Group.AUTHORIZED, rights);
        Assert.assertTrue(acl.isDenied(Acl.Group.AUTHORIZED, Acl.Permission.WRITE));
        Assert.assertTrue(acl.isDenied(Acl.Group.AUTHORIZED, Acl.Permission.READ));

        // Allow all to group
        acl.allow(Acl.Group.AUTHORIZED);
        Assert.assertTrue(acl.isAllowed(Acl.Group.AUTHORIZED, Acl.Permission.WRITE));
        Assert.assertTrue(acl.isAllowed(Acl.Group.AUTHORIZED, Acl.Permission.READ));

        // Deny all to group
        acl.deny(Acl.Group.AUTHORIZED);
        Assert.assertTrue(acl.isDenied(Acl.Group.AUTHORIZED, Acl.Permission.WRITE));
        Assert.assertTrue(acl.isDenied(Acl.Group.AUTHORIZED, Acl.Permission.READ));

        // Allow all to all
        acl.allowAll();
        Assert.assertTrue(acl.isAllowed(Acl.Group.AUTHORIZED, Acl.Permission.WRITE));
        Assert.assertTrue(acl.isAllowed(Acl.Group.EVERYONE, Acl.Permission.WRITE));
        Assert.assertTrue(acl.isAllowed(Acl.Group.OWNER, Acl.Permission.WRITE));

        // Deny all to all
        acl.denyAll();
        Assert.assertTrue(acl.isDenied(Acl.Group.AUTHORIZED, Acl.Permission.WRITE));
        Assert.assertTrue(acl.isDenied(Acl.Group.EVERYONE, Acl.Permission.WRITE));
        Assert.assertTrue(acl.isDenied(Acl.Group.OWNER, Acl.Permission.WRITE));
    }
}
