package gabywald.crypto.blockchain;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import java.util.List;
import java.util.Map;

import gabywald.global.json.JSONException;
import gabywald.global.json.JSONValue;
import gabywald.global.json.JSONifiable;

import java.util.ArrayList;
import java.util.Base64;

/**
 * Transaction of BlockChain.
 * <br/><a href="https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce">https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-2">https://github.com/CryptoKass/NoobChain-Tutorial-Part-2</a>
 * @author Gabriel Chandesris (2021)
 */
public class Transaction extends JSONifiable {
	/** This is also the hash of the transaction. */
	private String transactionId;
	/** senders address/public key. */
	private PublicKey sender;
	/** Recipients address/public key. */
	private PublicKey recipient;
	private float value;
	/** This is to prevent anybody else from spending funds in our wallet. */
	private byte[] signature;

	private List<TransactionInput> inputs = new ArrayList<TransactionInput>();
	private List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	/** A rough count of how many transactions have been generated. */
	private static int sequence = 0;

	// Constructor: 
	public Transaction(PublicKey from, PublicKey to, float value,  List<TransactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
	}

	/** 
	 * This Calculates the transaction hash (which will be used as its Id)
	 * @return
	 */
	private String calculateHash() {
		// Increase the sequence to avoid 2 identical transactions having the same hash
		Transaction.sequence++;
		return StringUtils.applySha256(
				StringUtils.getStringFromKey(this.sender) +
				StringUtils.getStringFromKey(this.recipient) +
				Float.toString(value) + Transaction.sequence
				);
	}

	/** 
	 * Applies ECDSA Signature and returns the result ( as bytes ). 
	 * @param privateKey
	 * @param input
	 * @return
	 */
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance(StringUtils.ECDSA, StringUtils.BC);
			// Signature.getInstance(StringUtils.CipherAlgorithm, StringUtils.CipherProvider);
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}

	/** 
	 * Verifies a String signature. 
	 * @param publicKey
	 * @param data
	 * @param signature
	 * @return
	 */
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance(StringUtils.ECDSA, StringUtils.BC);
			// Signature.getInstance(StringUtils.CipherAlgorithm, StringUtils.CipherProvider);
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	/** 
	 * Signs all the data we don't wish to be tampered with.
	 * @param privateKey
	 */
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtils.getStringFromKey(this.sender) 
				+ StringUtils.getStringFromKey(this.recipient) 
				+ Float.toString(this.value);
		try {
			this.signature = StringUtils.applyECDSASig(privateKey,data);
		} catch (BlockchainException e) {
			// e.printStackTrace();
			System.out.println( e.getMessage() );
			this.signature = null;
		}		
	}

	/**
	 * Verifies the data we signed hasn't been tampered with. 
	 * @return
	 */
	public boolean verifySignature() {
		String data = StringUtils.getStringFromKey(this.sender) 
				+ StringUtils.getStringFromKey(this.recipient) 
				+ Float.toString(this.value);
		return StringUtils.verifyECDSASig(this.sender, data, this.signature);
	}

	/** 
	 * Returns true if new transaction could be created.
	 * @param mapUTXOs
	 * @param minimumTransaction
	 * @return
	 */
	public boolean processTransaction(	final Map<String, TransactionOutput> mapUTXOs, 
										final float minimumTransaction) {

		if (this.verifySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		// Gather transaction inputs (Make sure they are unspent):
		// this.inputs.stream().forEach(ti -> ti.UTXO = UTXOs.get(ti.transactionOutputId) );
		for (TransactionInput it : inputs) {
			it.setTransactionOutput( mapUTXOs.get(it.getTransactionOutputId() ) );
		}

		// Check if transaction is valid:
		if (this.getInputsValue() < minimumTransaction) {
			System.out.println("#Transaction Inputs to small: " + this.getInputsValue());
			return false;
		}

		// Generate transaction outputs:
		float leftOver = this.getInputsValue() - value; // 
		// - Get value of inputs then the left over change:
		this.transactionId = this.calculateHash();
		// - Send value to recipient
		this.outputs.add(new TransactionOutput( this.recipient, this.value, this.transactionId));
		// - Send the left over 'change' back to sender
		this.outputs.add(new TransactionOutput( this.sender, leftOver, this.transactionId));	

		// Add outputs to Unspent list
		// this.outputs.stream().forEach(to -> mapUTXOs.put(to.id , to) );
		for (TransactionOutput to : outputs) {
			mapUTXOs.put(to.getId()  , to);
		}

		// Remove transaction inputs from UTXO lists as spent:
		this.inputs.stream().forEach(ti -> {
			// If Transaction can't be found skip it
			if ( ti.getTransactionOutput() != null) 
				{ mapUTXOs.remove(ti.getTransactionOutput().getId()); }
		} );

		return true;
	}

	/**
	 * Returns sum of inputs(UTXOs) values. 
	 * @return
	 */
	public float getInputsValue() {
		float total = 0;
		for (TransactionInput ti : inputs) {
			// If Transaction can't be found skip it
			if ( ti.getTransactionOutput() == null)  { continue; } 
			total += ti.getTransactionOutput().getValue();
		}
		return total;
	}

	/**
	 * Returns sum of outputs:
	 * @return
	 */
	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput to : outputs) {
			total += to.getValue();
		}
		return total;
	}

	public void setTransactionId(String transactionId) 
		{ this.transactionId = transactionId; }

	public String getTransactionId() 
		{ return this.transactionId; }

	public PublicKey getSender() 
		{ return this.sender; }

	public PublicKey getRecipient() 
		{ return this.recipient; }

	public float getValue() 
		{ return this.value; }

	public List<TransactionOutput> getOutputs() 
		{ return this.outputs; }

	public List<TransactionInput> getInputs() 
		{ return this.inputs; }

	@Override
	protected void setKeyValues() {
		this.put("transactionId", JSONValue.instanciate( this.transactionId.toString() ) );
		this.put("sender", JSONValue.instanciate( this.sender.toString() ) );
		this.put("recipient", JSONValue.instanciate( this.recipient.toString() ) );
		this.put("value", JSONValue.instanciate( this.value ) );
		// this.put("signature", JSONValue.instanciate( this.signature ) );
		// this.put("sequence", JSONValue.instanciate( this.sequence ) );
	}

	@Override
	protected <T extends JSONifiable> T reloadFrom(String json) 
			throws JSONException {
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sbToReturn = new StringBuilder();
		sbToReturn.append("transactionId").append(": ").append( this.transactionId.toString() ).append("\n");
		sbToReturn.append("sender").append(": ").append( this.sender.toString() ).append("\n");
		sbToReturn.append("recipient").append(": ").append( this.recipient ).append("\n");
		sbToReturn.append("value").append(": ").append( this.value ).append("\n");
		return sbToReturn.toString();
	}

}
