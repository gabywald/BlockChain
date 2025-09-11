package gabywald.crypto.blockchain.alt.pos;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * 
 * @author Gabriel Chandesris (2025)
 */
public class Transaction {
	public String sender;
	public String receiver;
	public int amount;
	public byte[] signature;

	public Transaction(String sender, String receiver, int amount) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
	}

	public void signTransaction(PrivateKey privateKey) throws Exception {
		String data = this.sender + this.receiver + this.amount;
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initSign(privateKey);
		sig.update(data.getBytes());
		this.signature = sig.sign();
	}

	public boolean verifyTransaction(PublicKey publicKey) throws Exception {
		String data = this.sender + this.receiver + this.amount;
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initVerify(publicKey);
		sig.update(data.getBytes());
		return sig.verify(signature);
	}
}
