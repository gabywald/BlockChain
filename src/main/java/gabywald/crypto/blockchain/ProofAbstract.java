package gabywald.crypto.blockchain;

/**
 * 
 * @author Gabriel Chandesris (2024). 
 */
public abstract class ProofAbstract implements ProofInterface {

	protected Block bloc;
	protected int difficulty;
	
	/**
	 * Default Constructor. 
	 * @param bloc
	 * @param difficulty
	 */
	protected ProofAbstract(Block bloc, int difficulty) {
		this.bloc = bloc;
		this.difficulty = difficulty;
	}
	
}
