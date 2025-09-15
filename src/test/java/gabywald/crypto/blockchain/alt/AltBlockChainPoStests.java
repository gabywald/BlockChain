package gabywald.crypto.blockchain.alt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.crypto.blockchain.alt.pos.BlockChain;

/**
 * Tests about Alt BlockChain PoS
 * @author Gabriel Chandesris (2025)
 */
class AltBlockChainPoStests {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test01() {
		BlockChain blockchain = new BlockChain();
		blockchain.addValidator(new Wallet("Alice", 100));
		blockchain.addValidator(new Wallet("Bob", 50));

		blockchain.addBlock("Transaction 1");
		blockchain.addBlock("Transaction 2");

		System.out.println("Blockchain valid: " + blockchain.isChainValid());
		
		Assertions.assertTrue( blockchain.isChainValid() );
	}
	
//	@Test
//	void test02() {
//		BlockChain blockchain = new BlockChain();
//		Wallet alice = new Wallet();
//		Wallet bob = new Wallet();
//
//		// Alice envoie 10 à Bob
//		Transaction tx = alice.createTransaction(bob.publicKey.toString(), 10);
//		blockchain.addTransaction(tx, alice.publicKey);
//
//		// Ajouter un bloc (validation par un validateur)
//		blockchain.addValidator(alice);
//		blockchain.addValidator(bob);
//		blockchain.addBlock();
//
//		System.out.println("Alice's balance: " + alice.balance);
//		System.out.println("Bob's balance: " + bob.balance);
// }

}
