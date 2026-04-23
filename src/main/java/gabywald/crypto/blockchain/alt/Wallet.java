package gabywald.crypto.blockchain.alt;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

import gabywald.crypto.blockchain.BlockchainException;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * @author Gabriel Chandesris (2025)
 */
public class Wallet {
    public PublicKey publicKey;
    public PrivateKey privateKey;
    public String name;
    public int balance;
    public int stake; // Pour PoS
    
    private Wallet(String name, int balance, int stake) {
    	KeyPair kp = this.generateKeyPair();
    	this.name = name;
        this.privateKey = (kp != null)?kp.getPrivate():null;
        this.publicKey = (kp != null)?kp.getPublic():null;
        this.balance = balance; // Solde initial
        this.stake = stake;     // Stake initial
    }

    public Wallet() 
    	{ this(null, 0, 0); }
    
	public Wallet(String name, int stake) 
		{ this(name, 0, stake); }

	private KeyPair generateKeyPair() {
        KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, " [Exception]generateKeyPair: " + e.getClass().getName() + ": " + e.getMessage() );
		}
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public Transaction createTransaction(String receiver, int amount) throws BlockchainException {
        if (this.balance < amount) { throw new BlockchainException("Insufficient balance"); }
        Transaction transaction = new Transaction(
        		this.publicKey.toString(),
            receiver,
            amount
        );
		try { transaction.signTransaction(privateKey); } 
		catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) 
			{ throw new BlockchainException("Signing Exception"); }
        this.balance -= amount;
        return transaction;
    }

	public String getName() 
		{ return this.name; }
}
