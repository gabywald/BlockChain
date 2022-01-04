package gabywald.crypto.blockchain;

import java.security.PublicKey;

/**
 * TransactionOutput of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce">https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-2">https://github.com/CryptoKass/NoobChain-Tutorial-Part-2</a>
 * @author Gabriel Chandesris (2021)
 */
public class TransactionOutput {
	private String id;
	/** Also known as the new owner of these coins. */
	private PublicKey recipient;
	/** The amount of coins they own */
	private float value;
	/** The id of the transaction this output was created in */
	private String parentTransactionId;
	
	/**
	 * Constructor
	 * @param reciepient
	 * @param value
	 * @param parentTransactionId
	 */
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.recipient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		
		this.id = StringUtils.applySha256(StringUtils.getStringFromKey(reciepient) 
				+ Float.toString(value) + parentTransactionId);
	}
	
	/**
	 * Check if coin belongs to you. 
	 * @param publicKey
	 * @return
	 */
	public boolean isMine(PublicKey publicKey) {
		return this.recipient.equals(publicKey);
		// return (publicKey == this.reciepient);
	}

	public String getId() 
		{ return this.id; }

	public PublicKey getRecipient() 
		{ return this.recipient; }

	public float getValue() 
		{ return this.value; }

	public String getParentTransactionId() 
		{ return this.parentTransactionId; }
	
	
	
}
