package net.ion.amazon.dynamo;

import java.util.List;

import junit.framework.TestCase;
import net.ion.amazon.AmzClientModule;
import net.ion.amazon.common.Credential;
import net.ion.amazon.sqs.SQSClient;
import net.ion.framework.db.Page;

public class TestDynamoClient extends TestCase {
	
	private Session session ; 
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DynamoClient dc = AmzClientModule.testInjector().getInstance(DynamoClient.class) ;
		this.session = dc.connectBy("Forum");
	}
	
	public void testInsertNode() throws Exception {
		Node node = insertTestNode();

		assertEquals(1, session.createQuery().eq("Name", "xxx").find().size());
		Node found = session.createQuery().eq("Name", "xxx").findOne();
		assertEquals(node.getString("Name"), found.getString("Name"));
	}

	public void testDelete() throws Exception {
		Node node = insertTestNode();

		session.createQuery().eq("Name", "xxx").remove();
		assertEquals(0, session.createQuery().eq("Name", "xxx").find().size());
	}
	
	public void testUpdate() throws Exception {
		Node node = insertTestNode();

		session.createQuery().eq("Name", "xxx").updateChain().put("name", "new").put("nfield", "nn").update() ;
		assertEquals(1, session.createQuery().eq("name", "new").eq("nfield", "nn").find().size());
	}
	
	public void testSelect() {
		insertTestMultiNode();
		
		NodeCursor nc = session.createQuery().eq("age", "11").find(Page.create(2, 1)) ;
		assertEquals(2, nc.toList().size()) ;
	}
	
	private Node insertTestNode()
	{
		Node node = session.newNode().put("Name", "xxx").put("age", 11).put("gender", true).put("array", new String[] { "a", "b" });
		session.commit();
		return node;
	}
	
	private void insertTestMultiNode()
	{
		session.newNode().put("Name", "xxx").put("age", "11").put("gender", true).put("array", new String[] { "a", "b" });
		session.commit();
		session.newNode().put("Name", "yyy").put("age", "11").put("gender", true).put("array", new String[] { "a", "b" });
		session.commit();
		session.newNode().put("Name", "zzz").put("age", "11").put("gender", true).put("array", new String[] { "a", "b" });
		session.commit();
	}
}
