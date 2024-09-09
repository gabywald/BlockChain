package gabywald.crypto.blockchain;

import java.util.Random;

/**
 * 
 * @author Gabriel Chandesris (2024). 
 */
public class ProofBasic extends ProofAbstract {
	
	// To Simplify with a low constant value compare to Proof Of Work !!
	public static final int BASIC_DIFFICULTY = 0;

	/**
	 * Default Constructor. 
	 * @param bloc
	 * @param difficulty Value is ignored (set to 0). 
	 * <àsee {@link ProofBasic#STAK_DIFFICULTY}
	 */
	public ProofBasic(Block bloc, Integer difficulty) 
		{ super(bloc, ProofBasic.BASIC_DIFFICULTY); }

	@Override
	public Block proofTreatment() {
		Block newBloc = new Block(this.bloc.getHash());
		newBloc.setTransactions(this.bloc.getTransactions());
		
		Random rand = new Random();
		
		String hash = BlockChain.calculateHash(this.bloc.getHash(), this.bloc.getTimeStamp(), this.bloc.getMerkleRoot(), rand.nextInt());
		
		System.out.println("Block Computed !! : '" + hash + "'" );
		newBloc.setComputedHash(hash);
		
		return newBloc;
	}
	
}
