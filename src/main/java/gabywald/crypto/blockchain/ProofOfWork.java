package gabywald.crypto.blockchain;

/**
 * 
 * @author Gabriel Chandesris (2023). 
 */
public class ProofOfWork implements ProofInterface {
	private Block bloc;
	private int difficulty;
	
	public ProofOfWork(Block bloc, int difficulty) {
		this.bloc = bloc;
		this.difficulty = difficulty;
	}
	
	@Override
	public Block proofTreatment() {
		return this.mining(this.difficulty);
	}
	
	public Block mining(int difficulty) {
		Block newBloc = new Block(this.bloc.getHash());
		newBloc.setTransactions(this.bloc.getTransactions());
		
		String hash = BlockChain.computeValidHash(this.bloc.getHash(), this.bloc.getTimeStamp(), this.bloc.getMerkleRoot(), difficulty);
		
		System.out.println("Block Mined!!! : '" + hash + "'" ); // "' \n prev '" + this.bloc.getPreviousHash() + "'");
		newBloc.setComputedHash(hash);
		
		return newBloc;
	}
	
}
