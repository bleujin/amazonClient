package net.ion.amazon.dynamo;

import com.google.inject.Inject;

import net.ion.amazon.common.Credential;

public class DynamoClient {
	
	private Credential cre ;
	@Inject
	private DynamoClient(Credential cre){
		this.cre = cre ;
	}
	
	public Session connectBy(String tableName) {
		return Session.create(this.cre, tableName);
	}
}
