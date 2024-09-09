package gabywald.crypto.blockchain;

import java.security.PublicKey;

import gabywald.global.json.JSONException;
import gabywald.global.json.JSONValue;
import gabywald.global.json.JSONifiable;

/**
 * TransactionOutput of BlockChain. 
 * @author Gabriel Chandesris (2021, 2024)
 */
public class TransactionOutput extends JSONifiable {
	private String id;
	/** Also known as the new owner of these coins. */
	private PublicKey recipient;
	/** The amount of coins they own */
	private float value;
	/** The id of the transaction this output was created in */
	private String parentTransactionId;
	
	/**
	 * Constructor
	 * @param recipient
	 * @param value
	 * @param parentTransactionId
	 */
	public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		
		this.id = StringUtils.applySha256(StringUtils.getStringFromKey(recipient) 
				+ Float.toString(value) + parentTransactionId);
	}
	
	/**
	 * Check if coin belongs to you. 
	 * @param publicKey
	 * @return
	 */
	public boolean isMine(PublicKey publicKey) {
		return this.recipient.equals(publicKey);
		// return (publicKey == this.recipient);
	}

	public String getId() 
		{ return this.id; }

	public PublicKey getRecipient() 
		{ return this.recipient; }

	public float getValue() 
		{ return this.value; }

	public String getParentTransactionId() 
		{ return this.parentTransactionId; }
	
	@Override
	protected void setKeyValues() {
		this.put("id", JSONValue.instanciate( this.id ) );
		this.put("recipient", JSONValue.instanciate( this.recipient.toString() ) );
		this.put("value", JSONValue.instanciate( this.value ) );
		this.put("parentTransactionId", JSONValue.instanciate(  this.parentTransactionId ) );
	}

	@Override
	protected <T extends JSONifiable> T reloadFrom(String json) 
			throws JSONException { return null; }
	
	@Override
	public String toString() {
		StringBuilder sbToReturn = new StringBuilder();
		sbToReturn.append("id").append(": ").append( this.id ).append("\n");
		// sbToReturn.append("recipient").append(": ").append( this.recipient.toString() ).append("\n");
		sbToReturn.append("value").append(": ").append( this.value ).append("\n");
		sbToReturn.append("parentTransactionId").append(": ").append( this.parentTransactionId ).append("\n");
		return sbToReturn.toString();
	}
	
}
