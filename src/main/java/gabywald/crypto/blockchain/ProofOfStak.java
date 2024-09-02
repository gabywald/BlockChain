package gabywald.crypto.blockchain;

/**
 * 
 * @author Gabriel Chandesris (2023-2024). 
 */
public class ProofOfStak extends ProofOfWork implements ProofInterface {
	
	// To Simplify with a low constant value compare to Proof Of Work !!
	public static final int STAK_DIFFICULTY = 0;

	public ProofOfStak(Block bloc) {
		super(bloc, ProofOfStak.STAK_DIFFICULTY);
	}

	@Override
	public Block proofTreatment() {
		return super.proofTreatment();
	}
	
}
