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
	/** Our data will be a simple message. */
	// private String data;
	/** As number of milliseconds since 1/1/1970. */
	private long timeStamp;
	private int nonce;

	private String merkleRoot;

	private List<Transaction> transactions = new ArrayList<Transaction>(); 

	/**
	 * Block Constructor. 
	 * @param data
	 * @param previousHash
	 */
	public Block(String data, String previousHash ) {
		// this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		// Making sure we do this after we set the other values.
		this.hash = this.calculateHash();
	}
	
	/**
	 * Block Constructor. 
	 * @param previousHash
	 */
	public Block(String previousHash ) {
		this(null, previousHash);
	}

	/**
	 * Calculate new hash based on blocks contents
	 * @return (null if exception apply internally). 
	 * @see StringUtils#applySha256(String)
	 */
	public String calculateHash() {
		String calculatedhash = StringUtils.applySha256( 
					previousHash +
					Long.toString(timeStamp) +
					Integer.toString(nonce) + 
					merkleRoot
					);
		return calculatedhash;
	}

	/**
	 * Mining block
	 * @param difficulty
	 */
	public void mineBlock(int difficulty) {
		this.merkleRoot = StringUtils.getMerkleRoot(this.transactions);
		// Create a string with difficulty * "0"
		String target = StringUtils.getDifficultyString(difficulty);
		while ( (this.hash == null) || ( ! this.hash.substring( 0, difficulty ).equals(target)) ) {
			this.nonce++;
			this.hash = this.calculateHash();
			// XXX NOTE if exception inside calculateHash : hash will be null !
		}
		System.out.println("Block Mined!!! : " + this.hash);
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
		// Process transaction and check if valid, unless block is genesis block then ignore.
		if (transaction == null) { return false; }		
		if (this.previousHash != "0") {
			if (transaction.processTransaction(UTXOs, minimumTransaction) != true) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		}
		this.transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}

	public String getHash() 
		{ return this.hash; }

	public String getPreviousHash() 
		{ return this.previousHash; }

	public String getMerkleRoot() 
		{ return this.merkleRoot; }

	public List<Transaction> getTransactions() 
		{ return this.transactions; }
	
	
	@Override
	protected void setKeyValues() {
		this.put("hash", JSONValue.instanciate( this.hash.toString() ) );
		this.put("previousHash", JSONValue.instanciate( this.previousHash.toString() ) );
		this.put("timeStamp", JSONValue.instanciate( this.timeStamp ) );
		this.put("nonce", JSONValue.instanciate( this.nonce ) );
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
		for (Transaction transaction : this.transactions) {
			sbToReturn.append("\t transaction").append(": ").append( transaction.toString() ).append("\n");
		}
		return sbToReturn.toString();
	}
}
