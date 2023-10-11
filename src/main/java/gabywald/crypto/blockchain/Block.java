package gabywald.crypto.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import gabywald.global.json.JSONException;
import gabywald.global.json.JSONValue;
import gabywald.global.json.JSONifiable;

/**
 * Block of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa">https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-1">https://github.com/CryptoKass/NoobChain-Tutorial-Part-1</a>
 * @author Gabriel Chandesris (2021)
 */
public class Block extends JSONifiable {

	private String hash;
	private String previousHash; 
	private String computedHash;
	/** Our data will be a simple message. */
	// private String data;
	/** As number of milliseconds since 1/1/1970. */
	private long timeStamp;

	private List<Transaction> transactions = new ArrayList<Transaction>(); 

	/**
	 * Block Constructor. 
	 * @param data
	 * @param previousHash
	 */
	public Block(String data, String previousHash) {
		// this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		// Making sure we do this after we set the other values.
		this.hash = this.calculateHash();
		// *** System.out.println("calculateHash: {" + this.hash + "} <=" );
		this.computedHash = "";
	}
	
	/**
	 * Block Constructor. 
	 * @param previousHash
	 */
	public Block(String previousHash) {
		this(null, previousHash);
	}
	
	/**
	 * Block Constructor. 
	 * @param data
	 * @param previous
	 */
	public Block(String data, Block previous) {
		this(data, previous.getHash());
	}
	
	/**
	 * Block Constructor. 
	 * @param previous
	 */
	public Block(Block previous) {
		this(null, previous);
	}

	private String calculateHash() { 
		String calculatedHash = BlockChain.calculateHash(this.previousHash, this.timeStamp, this.getMerkleRoot(), 0);
		// *** System.out.println("calculateHash: {" + this.hash + "}\t{" + calculatedHash + "}" );
		return calculatedHash;
	}
	
	String setComputedHash(String computedHash) {
		String toReturn  = this.computedHash;
		this.computedHash = computedHash;
		return toReturn;
	}
	
	String getComputedHash() {
		return this.computedHash;
	}

	/**
	 * Add transactions to this block. 
	 * @param transaction
	 * @param UTXOs
	 * @param minimumTransaction
	 * @return (boolean)
	 */
	public boolean addTransaction(	final Transaction transaction, 
									final Map<String, TransactionOutput> UTXOs, 
									final float minimumTransaction) {
		boolean toReturn = true;
		// Process transaction and check if valid, unless block is genesis block then ignore.
		if (transaction == null) { toReturn = false; }		
		if ( (toReturn) && (this.previousHash != "0") ) {
			if (transaction.processTransaction(UTXOs, minimumTransaction) != true) {
				System.out.println("Transaction failed to process. Discarded.");
				toReturn = false;
			}
		}
		if (toReturn) {
			this.transactions.add(transaction);
			System.out.println("Transaction Successfully added to Block");
		}
		this.hash = this.calculateHash();
		// System.out.println("calculateHash: {" + this.hash + "} <=" );
		return toReturn;
	}

	public String getHash() { 
		this.hash = this.calculateHash();
		return this.hash; 
	}

	public String getPreviousHash() 
		{ return this.previousHash; }

	public long getTimeStamp() 
		{ return this.timeStamp; } 
	
	public String getMerkleRoot() 
		{ return StringUtils.getMerkleRoot(this.transactions); }

	public List<Transaction> getTransactions() 
		{ return this.transactions; }
	
	void setTransactions(List<Transaction> transactions)
		{ this.transactions = transactions; }
	
	@Override
	protected void setKeyValues() {
		this.put("hash", JSONValue.instanciate( this.hash.toString() ) );
		this.put("previousHash", JSONValue.instanciate( this.previousHash.toString() ) );
		this.put("timeStamp", JSONValue.instanciate( this.timeStamp ) );
		this.put("transactions", JSONifiable.generateArray( this.transactions ) );
	}

	@Override
	protected <T extends JSONifiable> T reloadFrom(String json) 
			throws JSONException {
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sbToReturn = new StringBuilder();
		sbToReturn.append("hash").append(": ").append( this.hash.toString() ).append("\n");
		sbToReturn.append("previousHash").append(": ").append( this.previousHash.toString() ).append("\n");
		sbToReturn.append("timeStamp").append(": ").append( this.timeStamp ).append("\n");
		sbToReturn.append("transactions").append(": \n");
		for (Transaction transaction : this.transactions) 
			{ sbToReturn.append("\t transaction").append(": \n").append( transaction.toString() ).append("\n"); }
		return sbToReturn.toString();
	}
}
