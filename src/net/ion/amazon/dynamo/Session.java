package net.ion.amazon.dynamo;

import java.util.Map;

import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodb.model.Condition;
import com.amazonaws.services.dynamodb.model.DeleteItemRequest;
import com.amazonaws.services.dynamodb.model.DescribeTableRequest;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.PutItemResult;
import com.amazonaws.services.dynamodb.model.ReturnValue;
import com.amazonaws.services.dynamodb.model.ScanRequest;
import com.amazonaws.services.dynamodb.model.ScanResult;
import com.amazonaws.services.dynamodb.model.TableDescription;
import com.amazonaws.services.dynamodb.model.UpdateItemRequest;
import com.amazonaws.services.dynamodb.model.UpdateItemResult;

import net.ion.amazon.common.Credential;

public class Session {

	private AmazonDynamoDBClient inner;
	private String tableName;
	private TableDescription tableDescription;
	
	private Session(Credential cre, String tableName) {
		this.tableName = tableName ;
		this.inner = new AmazonDynamoDBClient(cre.toAmazonCredential());
		this.inner.setEndpoint("dynamodb.ap-northeast-1.amazonaws.com");
	
		getTableInformation();
	}
	
	private void getTableInformation()
    {
		tableDescription = inner.describeTable(
                new DescribeTableRequest().withTableName(tableName)).getTable();
        
		System.out.format("Name: %s:\n" +
                "Status: %s \n" + 
                "Provisioned Throughput (read capacity units/sec): %d \n" +
                "Provisioned Throughput (write capacity units/sec): %d \n",
                tableDescription.getTableName(),
                tableDescription.getTableStatus(),
                tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
                tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
    }

	public static Session create(Credential cre, String tableName) {
		return new Session(cre, tableName);
	}

	private Node current = null ;

	public Node newNode(){
		current = Node.create(this);
		return current ;
	}

	public Node newNode(Map<String, AttributeValue> map)
	{
		return Node.create(this, map);
	}

	public void commit() {
		PutItemResult res = inner.putItem(current.createRequest()) ;
        System.out.println("Result: " + res);
	}

	public String currentTableName() {
		return tableName;
	}

	public Query createQuery() {
		return new Query(this);
	}

	public ScanResult find(Map<String, Condition> filter)
	{
		ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(filter);
        return inner.scan(scanRequest);
	}

	public void delete(String val)
	{
		Key key = new Key().withHashKeyElement(new AttributeValue().withS(val));
		inner.deleteItem(new DeleteItemRequest(tableName, key));
	}

	public void update(String val, Map<String, AttributeValueUpdate> updateItems)
	{
		Key key = new Key().withHashKeyElement(new AttributeValue().withS(val));
		ReturnValue returnValues = ReturnValue.ALL_NEW;
	    
	    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	        .withTableName(tableName)
	        .withKey(key)
	        .withAttributeUpdates(updateItems)
	        .withReturnValues(returnValues);
	    
	    UpdateItemResult result = inner.updateItem(updateItemRequest);
	    
	    // Check the response.
        System.out.println("Printing item after multiple attribute update..." + result);
	}


	protected String getTableKeyName()
    {
		return tableDescription.getKeySchema().getHashKeyElement().getAttributeName();
    }
}
