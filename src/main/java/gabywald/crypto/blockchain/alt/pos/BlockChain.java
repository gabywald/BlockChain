package gabywald.crypto.blockchain.alt.pos;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Gabriel Chandesris (2025)
 */
public class BlockChain {
	private ArrayList<Block> chain;
	private ArrayList<Wallet> validators;

	public BlockChain() {
		this.chain = new ArrayList<>();
		this.validators = new ArrayList<>();
		this.chain.add(createGenesisBlock());
	}

	private Block createGenesisBlock() {
		return new Block("Genesis Block", "0", "Genesis");
	}

	public Block getLatestBlock() {
		return this.chain.get(this.chain.size() - 1);
	}

	public void addValidator(Wallet wallet) {
		this.validators.add(wallet);
	}

	public Wallet selectValidator() {
		int totalStake = this.validators.stream().mapToInt(w -> w.stake).sum();
		int random = new Random().nextInt(totalStake);
		int sum = 0;
		for (Wallet wallet : this.validators) {
			sum += wallet.stake;
			if (random < sum) {
				return wallet;
			}
		}
		return this.validators.get(0); // Fallback
	}

	public boolean isChainValid() {
		for (int i = 1; i < this.chain.size(); i++) {
			Block currentBlock = this.chain.get(i);
			Block previousBlock = this.chain.get(i - 1);
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				return false;
			}
			if (!currentBlock.previousHash.equals(previousBlock.hash)) {
				return false;
			}
		}
		return true;
	}
	
	public void penalizeValidator(Wallet validator) {
		validator.stake /= 2; // Réduction de 50% du stake
		System.out.println(validator.publicKey + " penalized. New stake: " + validator.stake);
	}
	
//	public void addBlock(String data) {
//		Wallet validator = selectValidator();
//		Block newBlock = new Block(data, this.getLatestBlock().hash, validator.publicKey);
//		chain.add(newBlock);
//		validator.stake += 1; // Récompense
//	}

	public void addBlock(String data) {
		Wallet validator = this.selectValidator();
		Block newBlock = new Block(data, this.getLatestBlock().hash, validator.publicKey);
		// Simuler une validation invalide (ex: bloc corrompu)
		if (newBlock.getData().contains("INVALID")) {
			penalizeValidator(validator);
			return; // Bloc rejeté
		}
		this.chain.add(newBlock);
		validator.stake += 1; // Récompense
	}
	
//	private ArrayList<Transaction> pendingTransactions = new ArrayList<>();
//
//	public void addTransaction(Transaction transaction, PublicKey senderPublicKey) throws Exception {
//		if (transaction.verifyTransaction(senderPublicKey)) {
//			pendingTransactions.add(transaction);
//		} else {
//			throw new Exception("Invalid transaction signature");
//		}
//	}
//
//	public void addBlock() throws Exception {
//		Wallet_v2 validator = selectValidator();
//		Block newBlock = new Block(this.pendingTransactions.toString(), this.getLatestBlock().hash, validator.publicKey);
//		chain.add(newBlock);
//		validator.stake += 1; // Récompense
//		pendingTransactions.clear(); // Transactions validées
//	}
}
