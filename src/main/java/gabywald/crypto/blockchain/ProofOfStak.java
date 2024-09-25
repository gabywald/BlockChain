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
		
		Random rand = new Random();
		
		
		// TODO include here use of ProofOfStakContainer and ProofOfStakValidator
		// TODO include wallet (PublicKey) in ProofOfStakValidator
		// TODO include 'incoming transaction' of gain (minimal transaction ?!) for selected Validator
		// TODO temporal release of Validator's Token (with 'incoming transaction'
		// TODO graph recognition (instead of temporal) for automatic validation (number of blocks / transactions ?)
		
		
		String hash = BlockChain.calculateHash(this.bloc.getHash(), this.bloc.getTimeStamp(), this.bloc.getMerkleRoot(), rand.nextInt());
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, " [ProofOfStak] Block Computed !! : '" + hash + "'" ); // "' \n prev '" + this.bloc.getPreviousHash() + "'");
		newBloc.setComputedHash(hash);
		
		return newBloc;
	}
	
}
