package gabywald.crypto.blockchain;

import java.security.PublicKey;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
public class ProofOfStakValidator {
	private float tokenValue = 0, tokenTime = 0;
	private PublicKey staker = null;
	
	public static ProofOfStakValidator buildProofOfStakValidator(final float value, final float time, final PublicKey staker) 
			throws BlockchainException {
		if (value <= 0.0) { throw new BlockchainException("Validator token value incorrect (" + value + ")"); }
		if (time <= 0.0)  { throw new BlockchainException("Validator token time incorrect (" + time + ")"); }
		if (staker == null)  { throw new BlockchainException("Validator staker time incorrect (" + staker + ")"); }
		// TODO check value is possible ('getBalance')
		return new ProofOfStakValidator(value, time, staker);
	}
	
	private ProofOfStakValidator(final float value, final float time, final PublicKey staker) {
		this.tokenValue = value;
		this.tokenTime  = time;
		this.staker = staker;
	}

	public float getTokenValue()	{ return this.tokenValue; }

	public float getTokenTime()		{ return this.tokenTime; }
	
	public PublicKey getStaker()	{ return this.staker; }
	
}
