package gabywald.crypto.blockchain.alt.pos;

/**
 * 
 * @author Gabriel Chandesris (2025)
 */
public class Wallet {
	public String publicKey;
	public int stake;

	public Wallet(String publicKey, int stake) {
		this.publicKey = publicKey;
		this.stake = stake;
	}
}
