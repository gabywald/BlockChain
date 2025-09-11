package gabywald.crypto.blockchain.alt.pow;

import java.util.ArrayList;

/**
 * 
 * @author Gabriel Chandesris (2025)
 */
public class BlockChain {
	private ArrayList<Block> chain;
	private int difficulty = 4; // Nombre de zéros requis / Difficulté initiale
	private final int targetBlockTime = 10 * 60; // 10 minutes en secondes
	private final int adjustmentInterval = 10; // Ajustement tous les 10 blocs

	public BlockChain() {
		this.chain = new ArrayList<>();
		this.chain.add(createGenesisBlock());
	}

	private Block createGenesisBlock() {
		return new Block("Genesis Block", "0");
	}

	public Block getLatestBlock() {
		return this.chain.get(this.chain.size() - 1);
	}

	public void adjustDifficulty() {
		if (this.chain.size() % adjustmentInterval == 0 && chain.size() > 0) {
			Block lastBlock = getLatestBlock();
			Block blockBefore = this.chain.get(this.chain.size() - this.adjustmentInterval - 1);
			long timeTaken = lastBlock.getTimeStamp() - blockBefore.getTimeStamp();
			long expectedTime = this.targetBlockTime * this.adjustmentInterval;

			if (timeTaken < expectedTime / 2) {
				this.difficulty++; // Trop rapide : augmenter la difficulté
			} else if (timeTaken > expectedTime * 2) {
				this.difficulty--; // Trop lent : réduire la difficulté
			}
			System.out.println("New difficulty: " + this.difficulty);
		}
	}

	public void addBlock(Block newBlock) {
		newBlock.previousHash = getLatestBlock().hash;
		newBlock.mineBlock(this.difficulty);
		chain.add(newBlock);
		adjustDifficulty(); // Ajustement après chaque bloc
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
}
