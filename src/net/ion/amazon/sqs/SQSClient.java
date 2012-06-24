package net.ion.amazon.sqs;

import java.util.List;
import java.util.Map;

import net.ion.amazon.AmzClientModule.Keynames;
import net.ion.amazon.common.Credential;
import net.ion.amazon.common.ServiceRegion;
import net.ion.framework.util.ListUtil;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SQSClient {

	private AmazonSQS sqs;
	private String queueURL ;
	private static List<String> ALL = ListUtil.toList("All");
	
	@Inject
	private SQSClient(Credential cre, @Named(Keynames.QueueRegion) ServiceRegion region, @Named(Keynames.QueueName) String newQueueName) {
		this.sqs = new AmazonSQSClient(cre.toAmazonCredential()) ;
		sqs.setEndpoint(region.getEndPoint()) ;
		
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(newQueueName);
		this.queueURL = sqs.createQueue(createQueueRequest).getQueueUrl() ; 
	}
	
	
	static SQSClient createWith(Credential cre, ServiceRegion region, String newQueueName){
		return new SQSClient(cre, region, newQueueName) ;
	}
	
	private String getQueueURL() {
		return queueURL ;
	}

	public List<String> listQueueUrl(){
		return sqs.listQueues().getQueueUrls() ;
	}
	
	
	public SQSClient setRegion(ServiceRegion region, String newQueueName){
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(newQueueName);
		
		this.queueURL = sqs.createQueue(createQueueRequest).getQueueUrl() ; 
		sqs.setEndpoint(region.getEndPoint()) ;
		return this ;
	}

	
	public SendMessageResult sendMessage(String msg) {
		SendMessageRequest request = new SendMessageRequest(getQueueURL(), msg);
		return sqs.sendMessage(request);
	}
	

	// Retrieves one or more messages from the specified queue, including the message body and message ID of each message. 
	// Messages returned by this action stay in the queue until you delete them. However, once a message is returned to a ReceiveMessage request, 
	// it is not returned on subsequent ReceiveMessage requests for the duration of the VisibilityTimeout . 
	// If you do not specify a VisibilityTimeout in the request, the overall visibility timeout for the queue is used for the returned messages. 
	public List<Message> receiveMessage(){
		ReceiveMessageRequest request = new ReceiveMessageRequest(getQueueURL());
		request.setAttributeNames(ALL);
		request.setMaxNumberOfMessages(5) ;
		
		return sqs.receiveMessage(request).getMessages();
	}

	public void shutdown(){
		sqs.shutdown() ;
	}
	
	
	public List<Message> popMessage(){
		List<Message> msgs = receiveMessage() ;

		List<DeleteMessageBatchRequestEntry> dentry = ListUtil.newList() ;
		for (Message msg : msgs) {
			dentry.add(new DeleteMessageBatchRequestEntry(msg.getMessageId(), msg.getReceiptHandle())) ;
		}
		if (dentry.size() > 0) sqs.deleteMessageBatch(new DeleteMessageBatchRequest(getQueueURL(), dentry)) ;
		
		return msgs ;
	}

	public Map<String, String> showAttributes() {
		// list the attributes of the queue we are interested in
		GetQueueAttributesRequest request = new GetQueueAttributesRequest(getQueueURL());
		request.setAttributeNames(ALL);
		return sqs.getQueueAttributes(request).getAttributes();
	}

	public void dropQueue() {
		DeleteQueueRequest request = new DeleteQueueRequest(getQueueURL()) ;
		sqs.deleteQueue(request) ;
	}

}


