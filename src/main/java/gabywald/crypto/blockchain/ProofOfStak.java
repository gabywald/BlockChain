package gabywald.crypto.blockchain;

import java.util.Random;

/**
 * 
 * TODO add stak interaction here !!
 * @author Gabriel Chandesris (2023-2024). 
 */
public class ProofOfStak implements ProofInterface {
	
	private Block bloc;
	// To Simplify with a low constant value compare to Proof Of Work !!
	public static final int STAK_DIFFICULTY = 0;

	public ProofOfStak(Block bloc) {
		this.bloc = bloc;
	}
	
	public ProofOfStak(Block bloc, int difficulty) {
		this(bloc);
	}

	@Override
	public Block proofTreatment() {
		Block newBloc = new Block(this.bloc.getHash());
		newBloc.setTransactions(this.bloc.getTransactions());
		
		Random rand = new Random();
		
		String hash = BlockChain.calculateHash(this.bloc.getHash(), this.bloc.getTimeStamp(), this.bloc.getMerkleRoot(), rand.nextInt());
		
		System.out.println("Block Computed !! : '" + hash + "'" ); // "' \n prev '" + this.bloc.getPreviousHash() + "'");
		newBloc.setComputedHash(hash);
		
		return newBloc;
	}
	
}
