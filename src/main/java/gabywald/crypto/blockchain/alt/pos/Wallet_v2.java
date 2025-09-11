package gabywald.crypto.blockchain.alt.pos;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 
 * @author Gabriel Chandesris (2025)
 */
public class Wallet_v2 {
	public PublicKey publicKey;
	public PrivateKey privateKey;
	public int balance;
	public int stake; // Pour PoS

	public Wallet_v2() throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		KeyPair pair = keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
		this.balance = 100; // Solde initial
		this.stake = 0;	// Stake initial
	}

	public Transaction createTransaction(String receiver, int amount) throws Exception {
		if (this.balance < amount) {
			throw new Exception("Insufficient balance");
		}
		Transaction transaction = new Transaction(
				this.publicKey.toString(),
			receiver,
			amount
		);
		transaction.signTransaction(privateKey);
		this.balance -= amount;
		return transaction;
	}
}
