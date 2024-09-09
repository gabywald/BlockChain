package gabywald.crypto.blockchain;

import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests about BlockChain / NoobChain. 
 * @author Gabriel Chandesris (2024)
 */
class NoobChainMoreTests {
	
	static boolean checkBalance(Wallet wallet, float attemptedValue, 
								Map<String, TransactionOutput> mapUTXOs) {
		float balanceWallet = wallet.getBalance( mapUTXOs );
		System.out.println("Wallet's balance is: " + balanceWallet);
		// Assertions.assertEquals(attemptedValue, balanceWallet);
		return (attemptedValue == balanceWallet);
	}
	
	static boolean checkBalances(	Wallet walletA, float attemptedValueA, 
									Wallet walletB, float attemptedValueB, 
									Map<String, TransactionOutput> mapUTXOs) {
		float balanceWalletA = walletA.getBalance( mapUTXOs );
		float balanceWalletB = walletB.getBalance( mapUTXOs );
		System.out.println("WalletA's balance is: " + balanceWalletA + " ? " + attemptedValueA);
		System.out.println("WalletB's balance is: " + balanceWalletB + " ? " + attemptedValueB);
		// Assertions.assertEquals(attemptedValueA, balanceWalletA);
		// Assertions.assertEquals(attemptedValueB, balanceWalletB);
		return (attemptedValueA == balanceWalletA && attemptedValueB == balanceWalletB);
	}

	@Test
	void testPart05() {
		
		List<Block> blockchain = BlockChain.build();
		
		Map<String, TransactionOutput> mapUTXOs = new HashMap<String, TransactionOutput>();
		
		int difficulty = 3;
		float minimumTransaction = 0.1f;
		
		// Setup Bouncy castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Create wallets:
		Wallet walletA = new Wallet();
		Wallet walletB = new Wallet();		
		Wallet coinbase = new Wallet();

		// Create genesis transaction, which sends 100 NoobCoin to walletA: 
		Transaction genesisTransaction = walletA.createGenesisTransactionBlock(coinbase, mapUTXOs, 100f);
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		Assertions.assertTrue( genesis.addTransaction(genesisTransaction, mapUTXOs, minimumTransaction) );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, genesis, new ProofOfWork(genesis, difficulty) ) );

		// Testing
		Block block1 = new Block(genesis.getHash());
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 100f, walletB, 000f, mapUTXOs) );
		
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		Transaction transaction1 = walletA.sendFunds(walletB.getPublicKey(), 40f, mapUTXOs);
		Assertions.assertNotNull( transaction1 );
		boolean bBlock1AddTransactionResult = block1.addTransaction(transaction1, mapUTXOs, minimumTransaction) ;
		Assertions.assertTrue( bBlock1AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block1, new ProofOfWork(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 60f, walletB, 40f, mapUTXOs) );
		
		boolean isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block2 = new Block(block1.getHash());
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		Transaction transaction2 = walletA.sendFunds(walletB.getPublicKey(), 1000f, mapUTXOs);
		Assertions.assertNull( transaction2 ); 
		boolean bBlock2AddTransactionResult = block2.addTransaction(transaction2, mapUTXOs, minimumTransaction);
		Assertions.assertFalse( bBlock2AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block2, new ProofOfWork(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 60, walletB, 40, mapUTXOs) );
		
		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block3 = new Block(block2.getHash());
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		Transaction transaction3 = walletB.sendFunds(walletA.getPublicKey(), 20f, mapUTXOs);
		Assertions.assertNotNull( transaction3 );
		boolean bBlock3AddTransactionResult = block3.addTransaction(transaction3, mapUTXOs, minimumTransaction);
		Assertions.assertTrue( bBlock3AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block3, new ProofOfWork(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 80, walletB, 20, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block4 = new Block(block3.getHash());
		System.out.println("\nWalletA is Attempting to send funds (10) to WalletB...");
		Transaction transaction4 = walletA.sendFunds(walletB.getPublicKey(), 10f, mapUTXOs);
		Assertions.assertNotNull( transaction4 );
		boolean bBlock4AddTransactionResult = block4.addTransaction(transaction4, mapUTXOs, minimumTransaction);
		Assertions.assertTrue( bBlock4AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block4, new ProofOfWork(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 70, walletB, 30, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block5 = new Block(block4.getHash());
		System.out.println("\nWalletB is Attempting to send funds (5) to WalletA...");
		Transaction transaction5 = walletB.sendFunds(walletA.getPublicKey(), 5f, mapUTXOs);
		Assertions.assertNotNull( transaction5 );
		boolean bBlock5AddTransactionResult = block5.addTransaction(transaction5, mapUTXOs, minimumTransaction);
		Assertions.assertTrue( bBlock5AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block5, new ProofOfWork(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 75, walletB, 25, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
	}
	
	@Test
	void testPart06() {
		
		List<Block> blockchain = BlockChain.build();
		
		Map<String, TransactionOutput> mapUTXOs = new HashMap<String, TransactionOutput>();
		
		int difficulty = ProofOfStak.STAK_DIFFICULTY;
		float minimumTransaction = 0.1f;
		
		// Setup Bouncy castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Create wallets:
		Wallet walletA = new Wallet();
		Wallet walletB = new Wallet();		
		Wallet coinbase = new Wallet();

		// Create genesis transaction, which sends 100 NoobCoin to walletA: 
		Transaction genesisTransaction = walletA.createGenesisTransactionBlock(coinbase, mapUTXOs, 100f);
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		Assertions.assertTrue( genesis.addTransaction(genesisTransaction, mapUTXOs, minimumTransaction) );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, genesis, new ProofOfStak(genesis, difficulty) ) );

		// Testing
		Block block1 = new Block(genesis.getHash());
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 100f, walletB, 000f, mapUTXOs) );
		
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		Transaction transaction1 = walletA.sendFunds(walletB.getPublicKey(), 40f, mapUTXOs);
		Assertions.assertNotNull( transaction1 );
		boolean bBlock1AddTransactionResult = block1.addTransaction(transaction1, mapUTXOs, minimumTransaction) ;
		Assertions.assertTrue( bBlock1AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block1, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 60f, walletB, 40f, mapUTXOs) );
		
		boolean isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block2 = new Block(block1.getHash());
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		Transaction transaction2 = walletA.sendFunds(walletB.getPublicKey(), 1000f, mapUTXOs);
		Assertions.assertNull( transaction2 );
		boolean bBlock2AddTransactionResult = block2.addTransaction(transaction2, mapUTXOs, minimumTransaction);
		Assertions.assertFalse( bBlock2AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block2, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 60, walletB, 40, mapUTXOs) );
		
		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block3 = new Block(block2.getHash());
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		Transaction transaction3 = walletB.sendFunds(walletA.getPublicKey(), 20f, mapUTXOs);
		Assertions.assertNotNull( transaction3 );
		boolean bBlock3AddTransactionResult = block3.addTransaction(transaction3, mapUTXOs, minimumTransaction);
		Assertions.assertTrue( bBlock3AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block3, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 80, walletB, 20, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block4 = new Block(block3.getHash());
		System.out.println("\nWalletA is Attempting to send funds (10) to WalletB...");
		Transaction transaction4 = walletA.sendFunds(walletB.getPublicKey(), 10f, mapUTXOs);
		Assertions.assertNotNull( transaction4 );
		boolean bBlock4AddTransactionResult = block4.addTransaction(transaction4, mapUTXOs, minimumTransaction);
		Assertions.assertTrue( bBlock4AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block4, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 70, walletB, 30, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block5 = new Block(block4.getHash());
		System.out.println("\nWalletB is Attempting to send funds (5) to WalletA...");
		Transaction transaction5 = walletB.sendFunds(walletA.getPublicKey(), 5f, mapUTXOs);
		Assertions.assertNotNull( transaction5 );
		boolean bBlock5AddTransactionResult = block5.addTransaction(transaction5, mapUTXOs, minimumTransaction);
		Assertions.assertTrue( bBlock5AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block5, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 75, walletB, 25, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
	}

	@Test
	void testPart07() {
		
		// TODO add here a walletC to give stak for transferts between A and B !!
		
		List<Block> blockchain = BlockChain.build();
		
		Map<String, TransactionOutput> mapUTXOs = new HashMap<String, TransactionOutput>();
		
		int difficulty = ProofOfStak.STAK_DIFFICULTY;
		float minimumTransaction = 0.1f;
		
		// Setup Bouncy castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Create wallets:
		Wallet walletA = new Wallet();
		Wallet walletB = new Wallet();		
		Wallet coinbase = new Wallet();

		// Create genesis transaction, which sends 100 NoobCoin to walletA: 
		Transaction genesisTransaction = walletA.createGenesisTransactionBlock(coinbase, mapUTXOs, 100f);
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		Assertions.assertTrue( genesis.addTransaction(genesisTransaction, mapUTXOs, minimumTransaction) );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, genesis, new ProofOfStak(genesis, difficulty) ) );

		// Testing
		Block block1 = new Block(genesis.getHash());
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 100f, walletB, 000f, mapUTXOs) );
		
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		Transaction transaction1 = walletA.sendFunds(walletB.getPublicKey(), 40f, mapUTXOs);
		Assertions.assertNotNull( transaction1 );
		boolean bBlock1AddTransactionResult = block1.addTransaction(transaction1, mapUTXOs, minimumTransaction) ;
		Assertions.assertTrue( bBlock1AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block1, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 60f, walletB, 40f, mapUTXOs) );
		
		boolean isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block2 = new Block(block1.getHash());
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		Transaction transaction2 = walletA.sendFunds(walletB.getPublicKey(), 1000f, mapUTXOs);
		Assertions.assertNull( transaction2 );
		boolean bBlock2AddTransactionResult = block2.addTransaction(transaction2, mapUTXOs, minimumTransaction);
		Assertions.assertFalse( bBlock2AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block2, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 60, walletB, 40, mapUTXOs) );
		
		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block3 = new Block(block2.getHash());
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		Transaction transaction3 = walletB.sendFunds(walletA.getPublicKey(), 20f, mapUTXOs);
		Assertions.assertNotNull( transaction3 );
		boolean bBlock3AddTransactionResult = block3.addTransaction(transaction3, mapUTXOs, minimumTransaction);
		Assertions.assertTrue( bBlock3AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block3, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 80, walletB, 20, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block4 = new Block(block3.getHash());
		System.out.println("\nWalletA is Attempting to send funds (10) to WalletB...");
		Transaction transaction4 = walletA.sendFunds(walletB.getPublicKey(), 10f, mapUTXOs);
		Assertions.assertNotNull( transaction4 );
		boolean bBlock4AddTransactionResult = block4.addTransaction(transaction4, mapUTXOs, minimumTransaction);
		Assertions.assertTrue( bBlock4AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block4, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 70, walletB, 30, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block5 = new Block(block4.getHash());
		System.out.println("\nWalletB is Attempting to send funds (5) to WalletA...");
		Transaction transaction5 = walletB.sendFunds(walletA.getPublicKey(), 5f, mapUTXOs);
		Assertions.assertNotNull( transaction5 );
		boolean bBlock5AddTransactionResult = block5.addTransaction(transaction5, mapUTXOs, minimumTransaction);
		Assertions.assertTrue( bBlock5AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block5, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainMoreTests.checkBalances(walletA, 75, walletB, 25, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
	}
	
}
