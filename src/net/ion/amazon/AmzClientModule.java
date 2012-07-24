package net.ion.amazon;

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
	
	public final static AmzClientModule test(){
		return new AmzClientModule("AKIAJSJZTYEK3S4OBNIA", "ch6VVYIOEQPTO+DkEyxJd8UHOdre/R5jlHnVZKo5") ;
	}
	
	public String accessKey(){
		return accessKey ;
	}
	
	public String secretKey(){
		return secretKey ;
	}
	
	public final static Injector testInjector(){
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
