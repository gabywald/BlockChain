package gabywald.crypto.blockchain;

import java.math.BigInteger;
import java.util.Random;

/**
 * 
 * @author Gabriel Chandesris (2023). 
 */
public class ProofOfWork implements ProofInterface {
	private Block bloc;
	private BigInteger target;
	
	public ProofOfWork(Block bloc, BigInteger target) {
		this.bloc = bloc;
		this.target = target;
	}
	
	@Override
	public void proofTreatment() {
		Block newBlock = this.mining();
		
	}
	
	public Block mining() {
		Block newBloc = new Block(this.bloc.getHash());
		newBloc.setTransactions(this.bloc.getTransactions());
		
		String hash = "";
		Random rand = new Random();
		long nonce = 0;
		
		while(this.isValid(hash)) {
			nonce = rand.nextLong();
			// TODO review that part !!
			hash = newBloc.calculateHash();
		}
		
		return newBloc;
	}
	
	private boolean isValid(String hash) {
		BigInteger hashInt = new BigInteger(hash, 16);
		return (hashInt.compareTo(this.target) < 0);
	}

}
