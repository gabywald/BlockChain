package gabywald.crypto.blockchain;

/**
 * 
 * @author Gabriel Chandesris (2023, 2024). 
 */
public class ProofOfWork extends ProofAbstract {
	
	/**
	 * Default Constructor. 
	 * @param bloc
	 * @param difficulty
	 */
	public ProofOfWork(Block bloc, Integer difficulty)
		{ super(bloc, difficulty); }
	
	@Override
	public Block proofTreatment() 
		{ return this.mining(this.difficulty); }
	
	private Block mining(int difficulty) {
		Block newBloc = new Block(this.bloc.getHash());
		newBloc.setTransactions(this.bloc.getTransactions());
		
		String hash = BlockChain.computeValidHash(this.bloc.getHash(), this.bloc.getTimeStamp(), this.bloc.getMerkleRoot(), difficulty);
		
		System.out.println("Block Mined!!! : '" + hash + "'" ); // "' \n prev '" + this.bloc.getPreviousHash() + "'");
		newBloc.setComputedHash(hash);
		
		return newBloc;
	}
	
}
