package gabywald.crypto.blockchain.alt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.crypto.blockchain.alt.pow.Block;
import gabywald.crypto.blockchain.alt.pow.BlockChain;

/**
 * Tests about Alt BlockChain PoW
 * @author Gabriel Chandesris (2025)
 */
class AltBlockChainPoWtests {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		BlockChain blockchain = new BlockChain();
		blockchain.addBlock(new Block("Transaction 1", blockchain.getLatestBlock().hash));
		blockchain.addBlock(new Block("Transaction 2", blockchain.getLatestBlock().hash));

		System.out.println("Blockchain valid: " + blockchain.isChainValid());
		
		Assertions.assertTrue( blockchain.isChainValid() );
	}

}
