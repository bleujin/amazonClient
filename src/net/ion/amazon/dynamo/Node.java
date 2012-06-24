package net.ion.amazon.dynamo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.PutItemRequest;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

public class Node {

	private Session session;
	private Map<String, Object> datas = MapUtil.newMap() ;
	
	private Node(Session session) {
		this.session = session ;
	}

	private Node(Session session, Map<String, AttributeValue> map) {
		this.session = session ;
		
		for (Entry<String, AttributeValue> entry : map.entrySet()) {
			datas.put(entry.getKey(), entry.getValue().getS());
		}
	}

	public static Node create(Session session) {
		return new Node(session);
	}

	public static Node create(Session session, Map<String, AttributeValue> map) {
		return new Node(session, map);
	}

	public Node put(String key, Object value) {
		datas.put(key, value) ;
		return this;
	}

	public PutItemRequest createRequest() {
		return new PutItemRequest(session.currentTableName(), newItem());
	}

	private Map<String, AttributeValue> newItem() {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		for (Entry<String, Object> entry : datas.entrySet()) {
			AttributeValue avalue = createAttributeValue(entry.getValue()) ;
			item.put(entry.getKey(), avalue);
		}
		
		return item;
	}

	private AttributeValue createAttributeValue(Object value) {
		if (value instanceof Integer){
			return new AttributeValue().withN(StringUtil.toString(value)) ;
		} else if (value.getClass().isArray()) {
			Object[] objs = (Object[]) value ;
			List<String> values = ListUtil.newList() ;
			for (Object obj : objs) {
				values.add(ObjectUtil.toString(obj)) ; 
			}
			return new AttributeValue().withSS(values); 
		} else{ 
			return new AttributeValue(ObjectUtil.toString(value));
		}
	}

	public String getString(String key)
	{
		return datas.get(key).toString();
	}
}
