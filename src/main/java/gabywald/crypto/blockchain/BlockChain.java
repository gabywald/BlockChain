package gabywald.crypto.blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gabywald.global.json.JSONException;
import gabywald.global.json.JSONifiable;

/**
 * 
 * @author Gabriel Chandesris (2021, 2024)
 */
public class BlockChain extends JSONifiable {
	
	private List<Block> internalBlocks = new ArrayList<Block>();
	
	public boolean add(Block block) 
		{ return this.internalBlocks.add(block); }
	
	public int size() 
		{ return this.internalBlocks.size(); }
	
	@Override
	protected void setKeyValues() 
		{ this.put("blocks", JSONifiable.generateArray( this.internalBlocks ) ); }

	@Override
	protected <T extends JSONifiable> T reloadFrom(String json) 
			throws JSONException { return null; }
	
	@Override
	public String toString() {
		StringBuilder sbToReturn = new StringBuilder();
		sbToReturn.append("blocks").append(": \n");
		for (Block block : this.internalBlocks) 
			{ sbToReturn.append("\t block").append(": \n").append( block.toString() ).append("\n"); }
		return sbToReturn.toString();
	}
	
	private BlockChain() { ; }
	
	/**
	 * Build a list of Blocks. 
	 * @return (List of Block)
	 */
	public static BlockChain build() { return new BlockChain(); }
	
	/**
	 * 
	 * @param blockchain
	 * @return (boolean)
	 */
	public static boolean isChainValidV1(final BlockChain blockchain) {
		Block currentBlock = null; 
		Block previousBlock = null;

		// Loop through blockchain to check hashes:
		for (int i = 1 ; i < blockchain.size() ; i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			// Compare registered hash and calculated hash:
			String calculatedHash = BlockChain.calculateHash(currentBlock.getPreviousHash(), currentBlock.getTimeStamp(), currentBlock.getMerkleRoot(), 0);
			if ( ! currentBlock.getHash().equals(calculatedHash) ) {
				System.out.println( currentBlock.getHash() + " / " + calculatedHash);
				System.out.println("Current Hashes not equal");			
				return false;
			}
			// Compare previous hash and registered previous hash
			if ( ! previousBlock.getHash().equals(currentBlock.getPreviousHash() ) ) {
				System.out.println( previousBlock.getHash() + " ?? " + currentBlock.getPreviousHash() );
				System.out.println("Previous Hashes not equal");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param blockchain
	 * @param genesisTransaction
	 * @param difficulty
	 * @return (boolean)
	 */
	public static boolean isChainValidV2(final BlockChain blockchain, final Transaction genesisTransaction, final int difficulty) {
		Block currentBlock = null; 
		Block previousBlock = null;
		String hashTarget = StringUtils.getDifficultyString(difficulty);
		
		// A temporary working list of unspent transactions at a given block state.
		TransactionOutputsContainer tempUTXOs = new TransactionOutputsContainer();
		tempUTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

		// Loop through blockchain to check hashes:
		for (int i = 1 ; i < blockchain.size() ; i++) {

			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			
			// NOTE ***** Could be "if ( (currentBlock == null) || (previousBlock == null) ) { return false; }"
			if ( (currentBlock == null) && (previousBlock != null) ) { return false; }
			if ( (currentBlock != null) && (previousBlock == null) ) { return false; }
			
			// Compare registered hash and calculated hash:
			String calculatedHash = BlockChain.calculateHash(currentBlock.getPreviousHash(), currentBlock.getTimeStamp(), currentBlock.getMerkleRoot(), 0);
			if ( ! currentBlock.getHash().equals(calculatedHash) ) {
				System.out.println("#Current Hashes not equal {" + currentBlock.getHash() + "} != {" + calculatedHash + "}");
				return false;
			}
			
			// Compare previous hash and registered previous hash
			if ( ! previousBlock.getHash().equals(currentBlock.getPreviousHash() ) ) {
				System.out.println("#Previous Hashes not equal {" + previousBlock.getHash() + "} != {" + currentBlock.getPreviousHash() + "}");
				return false;
			}
			
			// Check if hash is solved
			if ( ! currentBlock.getComputedHash().substring(0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}

			// Loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for (int t = 0 ; t < currentBlock.getTransactions().size() ; t++) {
				Transaction currentTransaction = currentBlock.getTransactions().get(t);

				if ( ! currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are not equal to outputs on Transaction(" + t + ") {" + currentTransaction.getInputsValue() + "} != {" + currentTransaction.getOutputsValue() + "}");
					return false; 
				}

				for (TransactionInput input: currentTransaction.getInputs()) {	
					tempOutput = tempUTXOs.getTransactionOutput(input.getTransactionOutputId() );

					if (tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}

					if (input.getTransactionOutput().getValue() != tempOutput.getValue()) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}

					tempUTXOs.remove(input.getTransactionOutputId() );
				}

				for (TransactionOutput output: currentTransaction.getOutputs()) 
					{ tempUTXOs.put(output.getId(), output); }

				if (currentTransaction.getOutputs().get(0).getRecipient() != currentTransaction.getRecipient()) {
					System.out.println("#Transaction(" + t + ") output recipient is not who it should be");
					return false;
				}
				
				if (currentTransaction.getOutputs().get(0).getRecipient() == currentTransaction.getSender()) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}

			}

		}
		System.out.println("Blockchain is valid");
		return true;
	}
	
//	/**
//	 * 
//	 * @param blockchain
//	 * @param genesisTransaction
//	 * @param difficulty
//	 * @param publicKey
//	 * @return
//	 * @throws BlockchainException
//	 * @deprecated("to test ?")
//	 */
//	public static float getBalanceFromHistory(  final BlockChain blockchain, 
//												final Transaction genesisTransaction, 
//												final int difficulty, 
//												final PublicKey publicKey) 
//				throws BlockchainException {
//		
//		if ( ! BlockChain.isChainValidV2(blockchain, genesisTransaction, difficulty) ) 
//			{ throw new BlockchainException("Chain is NOT valid !"); } 
//		
//		Block currentBlock = null; 
//		Block previousBlock = null;
//		
//		// A temporary working list of unspent transactions at a given block state.
//		TransactionOutputsContainer tempUTXOs = new TransactionOutputsContainer();
//		tempUTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));
//		
//		// Loop through blockchain to check hashes:
//		for (int i = 1 ; i < blockchain.size() ; i++) {
//
//			currentBlock = blockchain.get(i);
//			previousBlock = blockchain.get(i-1);
//			
//			if ( (currentBlock == null) || (previousBlock == null) ) 
//				{ throw new BlockchainException("A Block is NULL ! "); }
//
//			// Loop thru blockchains transactions:
//			for (int t = 0 ; t < currentBlock.getTransactions().size() ; t++) {
//				Transaction currentTransaction = currentBlock.getTransactions().get(t);
//				
//				// Removing send 
//				for (TransactionInput input: currentTransaction.getInputs()) 
//					{ tempUTXOs.remove( input.getTransactionOutputId() ); }
//
//				// Adding received
//				for (TransactionOutput output: currentTransaction.getOutputs()) 
//					{ tempUTXOs.put(output.getId(), output); }
//				
//			}
//			
//		}
//		
//		return tempUTXOs.getBalance(publicKey);
//	}

	public static boolean addBlock(final BlockChain blockchain, final ProofInterface ipi)
		{ return blockchain.add(ipi.proofTreatment()); }
	
	/**
	 * Calculate new hash based on blocks content. 
	 * @param prevHash
	 * @param timeStamp
	 * @param merkleRoot
	 * @param nonce
	 * @return (null if exception apply internally). 
	 * @see StringUtils#applySha256(String)
	 */
	public static String calculateHash(String prevHash, long timeStamp, String merkleRoot, int nonce) {
		String calculatedhash = StringUtils.applySha256( 
					prevHash +
					Long.toString(timeStamp) +
					Integer.toString(nonce) + 
					merkleRoot
					);
		return calculatedhash;
	}
	
	/**
	 * Compute Hash with given difficulty (longest step for ProofOfWork). 
	 * @param prevHash
	 * @param timeStamp
	 * @param merkleRoot
	 * @param difficulty
	 * @return Valid hash
	 * @see BlockChain#calculateHash(String, long, String, int)
	 */
	public static String computeValidHash(String prevHash, long timeStamp, String merkleRoot, int difficulty) {
		String hash = "";
		Random rand = new Random();
		int nonce = 0;
		
		while( ! BlockChain.isValid(hash, difficulty)) {
			nonce = rand.nextInt(); // this.nonce++;
			hash = BlockChain.calculateHash(prevHash, timeStamp, merkleRoot, nonce);
			// XXX NOTE if exception inside calculateHash : hash will be null !
		}
		return hash;
	}
	
	private static boolean isValid(String hash, int difficulty) {
		String target = StringUtils.getDifficultyString(difficulty);
		return (hash != null) && ( ! hash.isEmpty()) &&  (hash.substring( 0, difficulty ).equals(target));
	}

	public Block get(int i) {
		if ( (i < 0) || (i >= this.internalBlocks.size()) ) 
			{ return null; }
		return this.internalBlocks.get(i);
	}

}
