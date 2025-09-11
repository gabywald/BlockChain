package gabywald.crypto.blockchain.alt.pos;

import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Date;

/**
 * 
 * @author Gabriel Chandesris (2025)
 */
public class Block {
	public String hash;
	public String previousHash;
	public String validator;
	private String data;
	private long timeStamp;

	public Block(String data, String previousHash, String validator) {
		this.data = data;
		this.previousHash = previousHash;
		this.validator = validator;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}

	public Block(String string, String previousHash, PublicKey publicKey) {
		// TODO Auto-generated constructor stub
	}

	public String calculateHash() {
		String input = this.previousHash + this.timeStamp + getData() + this.validator;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getData() {
		return data;
	}
}
