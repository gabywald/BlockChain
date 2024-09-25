package gabywald.crypto.blockchain;

import java.security.Security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests about BlockChain / NoobChain. 
 * @author Gabriel Chandesris (2024)
 */
class NoobChainSimplePOWTests {
	
	@Test
	void testPartPoW() {
		
		BlockChain blockchain = BlockChain.build();
		TransactionOutputsContainer mapUTXOs = new TransactionOutputsContainer();
		int difficulty = 3;
		float minimumTransaction = 0.1f;
		Class<? extends ProofInterface> iProofClass = ProofOfWork.class;
		
		// Setup Bouncy castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Create wallets:
		Wallet walletA = new Wallet( "A" );
		Wallet walletB = new Wallet( "B" );
		Wallet coinbase = new Wallet( "coinbase" );

		// Create genesis transaction/block, which sends 100 NoobCoin to walletA: 
		Block genesis = walletA.createGenesisTransaction(100f, minimumTransaction, iProofClass, blockchain, difficulty, coinbase, mapUTXOs );
		Assertions.assertNotNull( genesis );
		Assertions.assertNotNull( genesis.getTransactions() );
		Assertions.assertNotNull( genesis.getTransactions().get(0) );
		Transaction genesisTransaction = genesis.getTransactions().get(0);
		Assertions.assertNotNull( genesisTransaction );
		Assertions.assertTrue( TransactionOutputsContainer.checkBalance(walletA, 100f, mapUTXOs) );
		Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 100f, walletB, 000f, mapUTXOs) );

		// Testing: A send 40 to B
		Block block1 = walletA.nextTransaction(40f, minimumTransaction, genesis, walletB, 
											   blockchain, difficulty, mapUTXOs, iProofClass);
		Assertions.assertNotNull( block1 );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
		// XXX Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 60f, walletB, 40f, mapUTXOs) );
		
		// Testing: A send 1000 to B (not working !)
		Block block2 = walletA.nextTransaction(1000f, minimumTransaction, block1, walletB, 
											   blockchain, difficulty, mapUTXOs, iProofClass);
		Assertions.assertNotNull( block2 );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
		// XXX Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 60f, walletB, 40f, mapUTXOs) );

		// Testing: B send 20 to A
		Block block3 = walletB.nextTransaction(20f, minimumTransaction, block2, walletA, 
											   blockchain, difficulty, mapUTXOs, iProofClass);
		Assertions.assertNotNull( block3 );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
		// XXX Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 80f, walletB, 20f, mapUTXOs) );
		
		// Testing: A send 10 to B
		Block block4 = walletA.nextTransaction(10f, minimumTransaction, block3, walletB, 
											   blockchain, difficulty, mapUTXOs, iProofClass);
		Assertions.assertNotNull( block4 );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
		// XXX Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 70f, walletB, 30f, mapUTXOs) );
		
		// Testing: B send 5 to A
		Block block5 = walletB.nextTransaction( 5f, minimumTransaction, block4, walletA, 
											   blockchain, difficulty, mapUTXOs, iProofClass);
		Assertions.assertNotNull( block5 );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
		// XXX Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 75f, walletB, 25f, mapUTXOs) );
	}
	
//	@Test
//	void testPartPoWbis() {
//		
//		BlockChain blockchain = BlockChain.build();
//		TransactionOutputsContainer mapUTXOs = new TransactionOutputsContainer();
//		int difficulty = 3;
//		float minimumTransaction = 0.1f;
//		Class<? extends ProofInterface> iProofClass = ProofOfWork.class;
//		
//		// Setup Bouncy castle as a Security Provider
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//
//		// Create wallets:
//		Wallet walletA = new Wallet( "A" );
//		Wallet walletB = new Wallet( "B" );
//		Wallet coinbase = new Wallet( "coinbase" );
//
//		// Create genesis transaction/block, which sends 100 NoobCoin to walletA: 
//		Block genesis = walletA.createGenesisTransaction(100f, minimumTransaction, iProofClass, blockchain, difficulty, coinbase, mapUTXOs );
//		Assertions.assertNotNull( genesis );
//		Assertions.assertNotNull( genesis.getTransactions() );
//		Assertions.assertNotNull( genesis.getTransactions().get(0) );
//		Transaction genesisTransaction = genesis.getTransactions().get(0);
//		Assertions.assertNotNull( genesisTransaction );
//		try {
//			float result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletA.getPublicKey());
//			Assertions.assertEquals(100.0f, result);
//			result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletB.getPublicKey());
//			Assertions.assertEquals(0.0f, result);
//		} catch (BlockchainException e) { Assertions.fail(e.getMessage()); }
//
//		// Testing: A send 40 to B
//		Block block1 = walletA.nextTransaction(40f, minimumTransaction, genesis, walletB, 
//											   blockchain, difficulty, mapUTXOs, iProofClass);
//		Assertions.assertNotNull( block1 );
//		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
//		try {
//			float result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletA.getPublicKey());
//			Assertions.assertEquals(60.0f, result);
//			result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletB.getPublicKey());
//			Assertions.assertEquals(40.0f, result);
//		} catch (BlockchainException e) { Assertions.fail(e.getMessage()); }
//		
//		// Testing: A send 1000 to B (not working !)
//		Block block2 = walletA.nextTransaction(1000f, minimumTransaction, block1, walletB, 
//											   blockchain, difficulty, mapUTXOs, iProofClass);
//		Assertions.assertNotNull( block2 );
//		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
//		try {
//			float result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletA.getPublicKey());
//			Assertions.assertEquals(60.0f, result);
//			result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletB.getPublicKey());
//			Assertions.assertEquals(40.0f, result);
//		} catch (BlockchainException e) { Assertions.fail(e.getMessage()); }
//
//		// Testing: B send 20 to A
//		Block block3 = walletB.nextTransaction(20f, minimumTransaction, block2, walletA, 
//											   blockchain, difficulty, mapUTXOs, iProofClass);
//		Assertions.assertNotNull( block3 );
//		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
//		try {
//			float result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletA.getPublicKey());
//			Assertions.assertEquals(80.0f, result);
//			result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletB.getPublicKey());
//			Assertions.assertEquals(20.0f, result);
//		} catch (BlockchainException e) { Assertions.fail(e.getMessage()); }
//		
//		// Testing: A send 10 to B
//		Block block4 = walletA.nextTransaction(10f, minimumTransaction, block3, walletB, 
//											   blockchain, difficulty, mapUTXOs, iProofClass);
//		Assertions.assertNotNull( block4 );
//		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
//		try {
//			float result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletA.getPublicKey());
//			Assertions.assertEquals(70.0f, result);
//			result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletB.getPublicKey());
//			Assertions.assertEquals(30.0f, result);
//		} catch (BlockchainException e) { Assertions.fail(e.getMessage()); }
//		
//		// Testing: B send 5 to A
//		Block block5 = walletB.nextTransaction( 5f, minimumTransaction, block4, walletA, 
//											   blockchain, difficulty, mapUTXOs, iProofClass);
//		Assertions.assertNotNull( block5 );
//		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
//		try {
//			float result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletA.getPublicKey());
//			Assertions.assertEquals(75.0f, result);
//			result = BlockChain.getBalanceFromHistory(blockchain, genesisTransaction, difficulty, walletB.getPublicKey());
//			Assertions.assertEquals(25.0f, result);
//		} catch (BlockchainException e) { Assertions.fail(e.getMessage()); }
//	}
	
}
