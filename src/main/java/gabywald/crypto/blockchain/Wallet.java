package gabywald.crypto.blockchain;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * Wallet of BlockChain. 
 * @author Gabriel Chandesris (2021, 2024)
 */
public class Wallet {

	private String name = null;
	private PrivateKey privateKey = null;
	private PublicKey publicKey = null;

	public Wallet(String name) {
		this.name = name;
		this.generateKeyPair(); 
	}
	
	public String getName()	
		{ return this.name; }
	
	public PrivateKey getPrivateKey() 
		{ return this.privateKey; }

	public PublicKey getPublicKey() 
		{ return this.publicKey; }

	/**
	 * Generate some KeyPair (Public and Private). 
	 */
	public void generateKeyPair() {
		try {
			// TODO learning how security works here !
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(StringUtils.ECDSA, StringUtils.BC);
			// KeyPairGenerator keyGen = KeyPairGenerator.getInstance(StringUtils.CipherAlgorithm, StringUtils.CipherProvider);
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair : 256 bytes provides an acceptable security level
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			// Set the public and private keys from the keyPair
			this.privateKey = keyPair.getPrivate();
			this.publicKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
			// throw new BlockchainException("StringUtils: NoSuchAlgorithmException: " + nsae.getMessage());
			Logger.printlnLog(LoggerLevel.LL_ERROR, " [Exception]generateKeyPair: " + e.getClass().getName() + ": " + e.getMessage() );
		} 
		
	}

	/**
	 * Returns balance and stores the UTXO's owned by this wallet in this.UTXOs
	 * @return (balance)
	 */
	public float getBalance(final TransactionOutputsContainer mapUTXOs) 
		{ return mapUTXOs.getBalance(this.publicKey); }

	/** 
	 * Generates and returns a new transaction from this wallet. 
	 * @param recipient
	 * @param value
	 * @param mapUTXOs
	 * @return (Transaction)
	 */
	public Transaction sendFunds(PublicKey recipient, float value, final TransactionOutputsContainer mapUTXOs) {
		// Gather balance and check funds.
		if (this.getBalance( mapUTXOs ) < value) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "#Not Enough funds to send transaction. Transaction Discarded.");
			return null;
		}
		// Create array list of inputs
		List<TransactionInput> inputs = new ArrayList<TransactionInput>();

		// Gather / group inputs transactions to get enough for output !
		// This makes inputs for the next Transaction !
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : mapUTXOs.entrySet()) {
			TransactionOutput to = item.getValue();
			total += to.getValue();
			inputs.add(new TransactionInput(to));
			if (total > value) { break; }
		}

		Transaction nextTransaction = new Transaction(this.publicKey, recipient, value, inputs);
		nextTransaction.generateSignature(this.privateKey);

		return nextTransaction;
	}
	
	public static final String GENESIS_TRANSACTION_ID = "0";
	
	public Block createGenesisTransaction(	final float startCoins, final float minimumTransaction, 
											final Class<? extends ProofInterface> proof, 
											final BlockChain blockchain, final int difficulty, 
											final Wallet coinbase, final TransactionOutputsContainer mapUTXOs) {
		// Create genesis transaction, which sends 'startCoins' NoobCoin to current wallet: 
		Transaction genesisTransaction = new Transaction(coinbase.getPublicKey(), this.getPublicKey(), startCoins, null);
		// Manually sign the genesis transaction
		genesisTransaction.generateSignature(coinbase.getPrivateKey());
		// Manually set the transaction id
		genesisTransaction.setTransactionId( Wallet.GENESIS_TRANSACTION_ID );
		// Manually add the Transactions Output
		TransactionOutput trOut = new TransactionOutput(genesisTransaction.getRecipient(), 
														genesisTransaction.getValue(), 
														genesisTransaction.getTransactionId() );
		genesisTransaction.getOutputs().add( trOut );
		// Manually add the Transactions Input
		TransactionInput trInp = new TransactionInput(trOut);
		genesisTransaction.getInputs().add( trInp );
		
		// Its important to store our first transaction in the UTXOs list.
		TransactionOutput firstOutputTransaction = genesisTransaction.getOutputs().get(0);
		mapUTXOs.put(firstOutputTransaction.getId(), firstOutputTransaction);
		
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Creating and Mining Genesis block... {" + this.name + "}");
		Block genesis = new Block( genesisTransaction.getTransactionId() ); // "0"
		genesis.addTransaction(genesisTransaction, mapUTXOs, minimumTransaction);
		try {
			Constructor<?> cons = proof.getConstructor(Block.class, Integer.class);
			BlockChain.addBlock(blockchain, (ProofInterface)cons.newInstance(genesis, difficulty) );
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{ e.printStackTrace(); }
		return genesis;
	}
	
	public Block nextTransaction(final float amount, final float minimumTransaction, 
								 final Block currentBlock, final Wallet recipient,
								 final BlockChain blockchain, final int difficulty, 
								 final TransactionOutputsContainer mapUTXOs, 
								 final Class<? extends ProofInterface> proof) {
		Block nextBlock = new Block(currentBlock.getHash());
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "\n" 	+ this.name + ":" + this.publicKey 
									+ " is Attempting to send funds (" + amount + ") to \n" 
									+ recipient.name + ":" + recipient.publicKey + "\n...");
		Transaction transaction = this.sendFunds(recipient.getPublicKey(), amount, mapUTXOs);
		nextBlock.addTransaction(transaction, mapUTXOs, minimumTransaction);
		// NOTE: subtile instanciation (equivalent in another programming language ?)
		// TODO parameter 'proof' HAS TO be part of a Class give in parameter ! 'Wallet' or 'BlockChain' ?
		// BlockChain.addBlock(blockchain, new ProofBasic(blockchain.get(blockchain.size()-1), difficulty) );
		try {
			Constructor<?> cons = proof.getConstructor(Block.class, Integer.class);
			BlockChain.addBlock(blockchain, (ProofInterface)cons.newInstance(blockchain.get(blockchain.size()-1), difficulty) );
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) { 
			// e.printStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append("[Wallet]nextTransaction: ").append( e.getClass().getName() ).append(": ").append( e.getMessage() );
			Logger.printlnLog(LoggerLevel.LL_ERROR, " **** [Exception] " + sb.toString() + " *****");
			// throw new BlockchainException( sb.toString() );
		}
		
		return nextBlock;
	}

}

