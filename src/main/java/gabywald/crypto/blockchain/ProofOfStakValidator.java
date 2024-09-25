package gabywald.crypto.blockchain;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
public class ProofOfStakValidator {
	private float tokenValue = 0, tokenTime = 0;
	
	public static ProofOfStakValidator buildProofOfStakValidator(final float value, final float time) 
			throws BlockchainException {
		if (value <= 0.0) { throw new BlockchainException("Validator token value incorrect (" + value + ")"); }
		if (time <= 0.0)  { throw new BlockchainException("Validator token time incorrect (" + time + ")"); }
		return new ProofOfStakValidator(value, time);
	}
	
	private ProofOfStakValidator(final float value, final float time) {
		this.tokenValue = value;
		this.tokenTime  = time;
	}

	public float getTokenValue() { return this.tokenValue; }

	public float getTokenTime()  { return this.tokenTime; }
	
}
