package javax.xml.bind;

public class JAXBException extends Throwable{

	private static final long serialVersionUID = 7152524622629240261L;
	public JAXBException(String msg){
		super(msg) ;
	}
	
	public JAXBException(Throwable cause){
		super(cause) ;
	}
}
