package net.ion.amazon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import net.ion.amazon.common.ServiceRegion;
import net.ion.amazon.s3.vfs.provider.S3FileProvider;
import net.ion.amazon.s3.vfs.provider.operations.S3FileOperationsProvider;

import org.apache.commons.vfs2.operations.FileOperationProvider;
import org.apache.commons.vfs2.provider.FileProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class AmzClientModule extends AbstractModule{

	
	private String accessKey ;
	private String secretKey ;
	
	private AmzClientModule(String accessKey, String secretKey){
		this.accessKey = accessKey ;
		this.secretKey = secretKey ;
	}
	
	public final static AmzClientModule test() throws FileNotFoundException, IOException{
		Properties props = new Properties() ;
		props.load(new FileInputStream("resource/AwsCredentials.properties")) ;
		return new AmzClientModule(props.getProperty("accessKey"), props.getProperty("secretKey")) ;
	}
	
	public String accessKey(){
		return accessKey ;
	}
	
	public String secretKey(){
		return secretKey ;
	}
	
	public final static Injector testInjector() throws FileNotFoundException, IOException{
		return Guice.createInjector(test()) ;
	}
	
	@Override
	protected void configure() {
		// bind(SQSClient.class).to(SQSClient.class) ;
		
		bind(FileProvider.class).to(S3FileProvider.class) ;
		bind(FileOperationProvider.class).to(S3FileOperationsProvider.class) ;
		
		bind(String.class).annotatedWith(Names.named(Keynames.AccessKey)).toInstance(accessKey) ;
		bind(String.class).annotatedWith(Names.named(Keynames.SecretKey)).toInstance(secretKey) ;
		
		bind(String.class).annotatedWith(Names.named(Keynames.QueueName)).toInstance("bleujin") ;
		bind(ServiceRegion.class).annotatedWith(Names.named(Keynames.QueueRegion)).toInstance(ServiceRegion.SQS.AsiaTokyo) ;
	}

	public final static class Keynames{
		public final static String AccessKey = "accessKey" ;
		public final static String SecretKey = "secretKey" ;
		
		public final static String QueueName = "queueName" ;
		public final static String QueueRegion = "queueRegion" ;

	} 
	
}
