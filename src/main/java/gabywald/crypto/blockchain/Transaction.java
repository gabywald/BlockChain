package gabywald.crypto.blockchain;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import java.util.List;

import gabywald.global.json.JSONException;
import gabywald.global.json.JSONValue;
import gabywald.global.json.JSONifiable;

import java.util.ArrayList;
import java.util.Base64;

/**
 * Transaction of BlockChain.
 * @author Gabriel Chandesris (2021, 2024)
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

	/**
	 * Constructor. 
	 * @param from
	 * @param to
	 * @param value
	 * @param inputs
	 */
	public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = (inputs != null)?inputs:this.inputs;
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
			// TODO change Exception class / type here ! (i.e. not RuntimeException !)
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
			// TODO change Exception class / type here ! (i.e. not RuntimeException !)
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
	public boolean processTransaction(	final TransactionOutputsContainer mapUTXOs, 
										final float minimumTransaction) {

		if (this.verifySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		// Gather transaction inputs (Make sure they are unspent):
		for (TransactionInput ti : this.inputs)
			{ ti.setTransactionOutput( mapUTXOs.getTransactionOutput( ti.getTransactionOutputId() ) ); }

		// Check if transaction is valid:
		if (this.getInputsValue() < minimumTransaction) {
			System.out.println("#Transaction Inputs to small: [" + this.getInputsValue() + "]");
			return false;
		}

		// Generate transaction outputs:
		float leftOver = this.getInputsValue() - this.value;
		// - Get value of inputs then the left over change:
		this.transactionId = this.calculateHash();
		// - Send value to recipient
		this.outputs.add(new TransactionOutput( this.recipient, this.value, this.transactionId));
		// - Send the left over 'change' back to sender
		this.outputs.add(new TransactionOutput( this.sender, leftOver, this.transactionId));
		
		System.out.println( "Inputs - value // " + this.getInputsValue() + " - " + this.value );
		System.out.println( "\t Adding TO rcp // " + this.value );
		System.out.println( "\t Adding TO snd // " + leftOver );

		// Add outputs to Unspent list
		for (TransactionOutput to : this.outputs) { mapUTXOs.put(to.getId(), to); }

		// Remove transaction inputs from UTXO lists as spent:
		this.inputs.stream().forEach(ti -> {
			// TODO [bug?!] studies part for "bad removing" ??
			System.out.println( "Removal... " );
			if ( ti.getTransactionOutput() != null) {
				TransactionOutput to = mapUTXOs.getTransactionOutput(ti.getTransactionOutput().getId());
				System.out.println( "\t Removing original TO // " + to.getValue() );
			}
			
			// If Transaction can't be found skip it
			if ( ti.getTransactionOutput() != null) 
				{ mapUTXOs.remove(ti.getTransactionOutput().getId()); }
		} );

		return true;
	}

	/**
	 * Returns sum of inputs values.
	 * @return Total of Inputs' values.
	 */
	public float getInputsValue() {
		float total = 0;
		for (TransactionInput ti : inputs) {
			// If Transaction can't be found skip it
			if ( ti.getTransactionOutput() != null)
				{ total += ti.getTransactionOutput().getValue(); }
		}
		return total;
	}

	/**
	 * Returns sum of outputs values.
	 * @return Total of Outputs' values. 
	 */
	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput to : outputs)
			{ total += to.getValue(); }
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
	
	public List<TransactionInput> getInputs() 
		{ return this.inputs; }

	public List<TransactionOutput> getOutputs() 
		{ return this.outputs; }

	@Override
	protected void setKeyValues() {
		this.put("transactionId", JSONValue.instanciate( this.transactionId.toString() ) );
		this.put("sender", JSONValue.instanciate( this.sender.toString() ) );
		this.put("recipient", JSONValue.instanciate( this.recipient.toString() ) );
		this.put("value", JSONValue.instanciate( this.value ) );
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
