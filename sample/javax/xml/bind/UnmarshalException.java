package javax.xml.bind;

public class UnmarshalException extends Throwable{

	private static final long serialVersionUID = -4174433129519226595L;

	public UnmarshalException(String msg){
		super(msg) ;
	}
	
	public UnmarshalException(Throwable cause){
		super(cause) ;
	}
}
