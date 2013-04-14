package policy;

public class SenderInitTransferPolicy extends TransferPolicy {
	
	private int THRESHOLD = 2;
	private int queue_length;
	
	public SenderInitTransferPolicy(int queue_length){
		super();
		this.queue_length = queue_length;
	}
	
	public SenderInitTransferPolicy(int queue_length, int threshold){
		this(queue_length);
		this.THRESHOLD = threshold;
	}
	
	@Override
	public boolean isTransferable() {
		return this.queue_length > THRESHOLD;
	}
}
