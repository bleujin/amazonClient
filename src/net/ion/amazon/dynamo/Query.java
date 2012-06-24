package net.ion.amazon.dynamo;

import java.util.List;
import java.util.Map;

import net.ion.framework.db.Page;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;

import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodb.model.ComparisonOperator;
import com.amazonaws.services.dynamodb.model.Condition;
import com.amazonaws.services.dynamodb.model.ScanResult;

public class Query
{
	Map<String, Condition> scanFilter = MapUtil.newMap();
    Map<String, AttributeValueUpdate> updateItems = MapUtil.newMap();
	 
	Session session;
    
	public Query(Session session)
	{
		this.session = session;
	}
	
	public Query eq(String key, String val)
	{
		Condition condition = new Condition()
        	.withComparisonOperator(ComparisonOperator.EQ.toString())
        	.withAttributeValueList(new AttributeValue().withS(val));
        
        scanFilter.put(key, condition);
        
        return this;
	}
	
	public List<Node> find()
	{
		List<Node> nodes = ListUtil.newList();
		ScanResult result = session.find(scanFilter);
		
	    // Check the response.
        System.out.println("Printing item after multiple attribute find..." + result);

		List<Map<String,AttributeValue>> items = result.getItems();
		for (int i = 0; i < items.size(); i++)
		{
			Node node = session.newNode(items.get(i));
			nodes.add(node);
		}
		
		return nodes;
	}

	public NodeCursor find(Page page)
	{
		List<Node> nodes = find();
		
		return new NodeCursor(nodes, page);
	}

	public Node findOne()
	{
		ScanResult result = session.find(scanFilter);
		
		List<Map<String,AttributeValue>> items = result.getItems();
		if (items.size() > 0)
		{
			return session.newNode(items.get(0));
		}
		
		return null;
	}

	public void remove()
	{
		ScanResult result = session.find(scanFilter);
	
		String key = session.getTableKeyName();
		List<Map<String,AttributeValue>> items = result.getItems();
		for (int i = 0; i < items.size(); i++)
		{
			Map<String,AttributeValue> item = items.get(i);
			session.delete(item.get(key).getS());
		}
	}

	public Query updateChain()
	{
		return this;
	}

	public Query put(String key, String val)
	{
		updateItems.put(key, 
                new AttributeValueUpdate()
                    .withValue(new AttributeValue().withS(val)));
		
		return this;
	}

	public void update()
	{
		ScanResult result = session.find(scanFilter);
		
		String key = session.getTableKeyName();
		List<Map<String,AttributeValue>> items = result.getItems();
		for (int i = 0; i < items.size(); i++)
		{
			Map<String,AttributeValue> item = items.get(i);
			session.update(item.get(key).getS(), updateItems);
		}
	}

}
