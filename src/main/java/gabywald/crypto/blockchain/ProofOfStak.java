package gabywald.crypto.blockchain;

import java.util.Random;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * TODO add stak interaction here !!
 * @author Gabriel Chandesris (2023-2024). 
 */
public class ProofOfStak extends ProofAbstract {
	
	// To Simplify with a low constant value compare to Proof Of Work !!
	public static final int STAK_DIFFICULTY = 0;

	/**
	 * Default Constructor. 
	 * @param bloc
	 * @param difficulty Value is ignored (set to 0). 
	 * <àsee {@link ProofOfStak#STAK_DIFFICULTY}
	 */
	public ProofOfStak(Block bloc, Integer difficulty) 
		{ super(bloc, ProofOfStak.STAK_DIFFICULTY); }

	@Override
	public Block proofTreatment() {
		Block newBloc = new Block(this.bloc.getHash());
		newBloc.setTransactions(this.bloc.getTransactions());
		
		// TODO include here use of ProofOfStakContainer and ProofOfStakValidator
//		ProofOfStakValidator posV = ProofOfStakContainer.getInstance().selectValidator();
//		if (posV == null) {
//			Logger.printlnLog(LoggerLevel.LL_ERROR, " [ProofOfStak] No Validator !!" );
//			return newBloc;
//		}
		// TODO include wallet (PublicKey) in ProofOfStakValidator
		// TODO removal Transaction when in StakValidator (temporary Wallet, or not !?)
		// TODO restitution Transaction when time is passed
		// TODO include 'incoming transaction' of gain (minimal transaction ?!) for selected Validator
		// TODO temporal release of Validator's Token (with 'incoming transaction'
		// TODO graph recognition (instead of temporal) for automatic validation (number of blocks / transactions ?)
		
		Random rand = new Random();
		String hash = BlockChain.calculateHash(this.bloc.getHash(), this.bloc.getTimeStamp(), this.bloc.getMerkleRoot(), rand.nextInt());
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, " [ProofOfStak] Block Computed !! : '" + hash + "'" ); // "' \n prev '" + this.bloc.getPreviousHash() + "'");
		newBloc.setComputedHash(hash);
		
		return newBloc;
	}
	
}
