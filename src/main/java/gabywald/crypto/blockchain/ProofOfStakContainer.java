package gabywald.crypto.blockchain;

import java.util.List;
import java.util.Random;
import java.security.PublicKey;
import java.util.ArrayList;


/**
 * 
 * <br><i>DPsingleton</i>
 * @author Gabriel Chandesris (2024)
 */
public class ProofOfStakContainer {
	private static ProofOfStakContainer instance = null;
	
	private ProofOfStakContainer() { ; }
	
	public static ProofOfStakContainer getInstance() {
		if (ProofOfStakContainer.instance == null) 
			{ ProofOfStakContainer.instance = new ProofOfStakContainer(); }
		return ProofOfStakContainer.instance;
	}
	
	public static ProofOfStakValidator selectStaticValidator() 
		{ return ProofOfStakContainer.getInstance().selectValidator(); }
	
	/** Set Of possible validators. */
	private List<ProofOfStakValidator> validators = new ArrayList<ProofOfStakValidator>();
	
	public boolean addValidator(final float value, final float time, final PublicKey staker) {
		boolean isBuild = true;
		try { validators.add( ProofOfStakValidator.buildProofOfStakValidator(value, time, staker) ); } 
		catch (BlockchainException e) {
			// e.printStackTrace();
			// log error here !!
			isBuild = false;
		}
		return isBuild;
	}
	
	public int size() { return this.validators.size(); }
	
	public ProofOfStakValidator selectValidator() {
		
		// TODO case when 'validators' is empty ?! (select a default one or insert regularly one ??)
		if (this.validators.size() == 0) { return null; }
		
		float totalStakWithTime = 0;
		for (ProofOfStakValidator validator : this.validators) 
			{ totalStakWithTime += validator.getTokenValue() * validator.getTokenTime(); }
		
		Random rand = new Random();
		int randomValue = rand.nextInt((int) totalStakWithTime);
		
		float weightSum = 0;
		for (ProofOfStakValidator validator : this.validators) {
			weightSum += validator.getTokenValue() * validator.getTokenTime();
			// More time in Container : more luck to be selected !
			if (weightSum >= randomValue) {
				ProofOfStakValidator selectedValidator = validator;
				this.validators.remove(selectedValidator);
				return selectedValidator;
			}
		}
		return null; // TODO avoid this ?!
	}

	public void clear() { this.validators.clear(); }
}
