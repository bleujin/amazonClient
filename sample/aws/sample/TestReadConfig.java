package aws.sample;

import java.io.InputStream;
import java.io.StringWriter;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.StringUtil;

import junit.framework.TestCase;

public class TestReadConfig extends TestCase{

	public void testRead() throws Exception {
		InputStream input = TestReadConfig.class.getResourceAsStream("/AwsCredentials.properties") ;
		Debug.line(IOUtil.toString(input)) ;
		input.close() ;
	}
}
