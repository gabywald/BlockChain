package gabywald.crypto.blockchain.alt.pow;

import java.util.Date;
import java.security.MessageDigest;

/**
 * 
 * @author Gabriel Chandesris (2025)
 */
public class Block {
	public String hash;
	public String previousHash;
	private String data;
	private long timeStamp;
	private int nonce;

	public Block(String data, String previousHash) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}

	public String calculateHash() {
		String input = this.previousHash + getTimeStamp() + this.nonce + this.data;
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

	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0');
		while (!hash.substring(0, difficulty).equals(target)) {
			this.nonce++;
			this.hash = calculateHash();
		}
		System.out.println("Block mined: " + this.hash);
	}

	public long getTimeStamp() {
		return this.timeStamp;
	}

}
