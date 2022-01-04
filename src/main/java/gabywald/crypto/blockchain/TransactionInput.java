package gabywald.crypto.blockchain;

/**
 * TransactionInput of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce">https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-2">https://github.com/CryptoKass/NoobChain-Tutorial-Part-2</a>
 * @author Gabriel Chandesris (2021)
 */
public class TransactionInput {
	/** Reference to TransactionOutputs -> transactionId */
	private String transactionOutputId = null;
	/** Contains the Unspent transaction output */
	private TransactionOutput to = null;
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	public String getTransactionOutputId() 
		{ return this.transactionOutputId; }

	public TransactionOutput getTransactionOutput() 
		{ return this.to; }
	
	void setTransactionOutput(TransactionOutput to) 
		{ this.to = to; }
}
