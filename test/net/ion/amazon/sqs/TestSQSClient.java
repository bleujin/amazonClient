package net.ion.amazon.sqs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import net.ion.amazon.AmzClientModule;
import net.ion.amazon.common.Credential;
import net.ion.amazon.common.ServiceRegion;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.ListUtil;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestSQSClient extends TestCase {

	private SQSClient client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		client = AmzClientModule.testInjector().getInstance(SQSClient.class) ;
	}

	@Override
	protected void tearDown() throws Exception {
		client.shutdown();
		super.tearDown();
	}
	
	public void testListQueueURL() throws Exception {
		List<String> urls = client.listQueueUrl() ;
		Debug.line(urls) ;
	}

	private SendMessageResult sendMessage(int max) throws ParseException {
		SendMessageResult result = null;
		for (int i = 0; i < max; i++) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			result = client.sendMessage("a random message sent at " + format.format(new Date()));
		}
		return result ;
	}

	public void testSendMessage() throws Exception {
		SendMessageResult result = sendMessage(1) ;
		assertEquals(true, result != null) ;
		List<Message> msg  = client.popMessage() ;
		Debug.line(msg) ;
	}
	
	public void testShowAttribute() throws Exception{
		sendMessage(1) ;
		Map<String, String> attributes = client.showAttributes(); // show the attributes of the queue
		Debug.line("Messages in the queue: " + attributes.get("ApproximateNumberOfMessages"));
		Debug.line("Messages not visible: " + attributes.get("ApproximateNumberOfMessagesNotVisible"));
		assertEquals(true, attributes.get("ApproximateNumberOfMessages").compareTo("0") > 0);
		client.popMessage() ;
	}

	public void testReceive() throws Exception {
		sendMessage(1) ;
		int retry = 3 ;
		boolean received = false ;
		for (int i = 0; i < retry; i++) {
			List<Message> msgs = client.receiveMessage();
			if (msgs.size() > 0) {
				Debug.line(msgs.get(0).getMessageId(), msgs.get(0).getBody());
				received = true ;
			}
		}
		
		if (! received) fail() ;
	}

	public void testPopMessage() throws Exception {
		sendMessage(2) ;
		List<Message> poped = ListUtil.newList() ;
		for (int i = 0; i < 5; i++) {
			poped.addAll(client.popMessage());
		}
		assertEquals(0, client.popMessage().size());
	}

	
	public void xtestDropQueue() throws Exception {
		client.dropQueue() ;
	}
	
	
	public void testSpeed() throws Exception {
		ExecutorService es = Executors.newCachedThreadPool() ;
		final TestSQSClient self = new TestSQSClient() ;
		self.setUp() ;
		for (int i = 0; i < 10 ; i++) {
			es.submit(new Callable<Integer>() {
				public Integer call() throws Exception {
					self.sendMessage(10) ;
					Debug.line(new Date()) ;
					return 1000;
				}
			}) ;
		}
		new InfinityThread().startNJoin() ;
	}
	
	public void testSpeedGet() throws Exception {
		
	}

}
