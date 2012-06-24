package net.ion.amazon.common;

import net.ion.amazon.AmzClientModule;
import net.ion.framework.util.StringUtil;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Credential {

	private String accessKey ;
	private String secretKey ;
	
	@Inject
	private Credential(@Named(AmzClientModule.Keynames.AccessKey) String accessKey, @Named(AmzClientModule.Keynames.SecretKey) String secretKey) {
		this.accessKey = accessKey ;
		this.secretKey = secretKey ;
	}
	
	public final static Credential create(String accessKey, String secretKey){
		if (StringUtil.isEmpty(accessKey) || StringUtil.isEmpty(secretKey)) {
			throw new IllegalArgumentException("Empty AWS credentials");
		}
		
		return new Credential(accessKey, secretKey) ;
	}
	
	public AWSCredentials toAmazonCredential(){
		return new BasicAWSCredentials(accessKey, secretKey) ;
	}
	
	public org.jets3t.service.security.AWSCredentials toJet3Credential(){
		return new org.jets3t.service.security.AWSCredentials(accessKey, secretKey) ;
	}
}
