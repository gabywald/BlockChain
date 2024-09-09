package gabywald.crypto.blockchain;

import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gabywald.global.json.JSONifiable;

/**
 * Tests about BlockChain / NoobChain. 
 * @author Gabriel Chandesris (2021, 2023, 2024)
 */
class NoobChainFirstTests {
	
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

	/**
	 * Test : block. 
	 */
	@Test
	void testPart01() {
		Block genesisBlock = new Block("Hi im the first block", "0");
		Assertions.assertNotNull( genesisBlock );
		System.out.println("Hash for block 1 : " + genesisBlock.getHash() );
		Assertions.assertNotNull( genesisBlock.getHash() );
		
		Block secondBlock = new Block("Yo im the second block", genesisBlock );
		Assertions.assertNotNull( secondBlock );
		System.out.println("Hash for block 2 : " + secondBlock.getHash() );
		Assertions.assertNotNull( secondBlock.getHash() );
		
		Block thirdBlock = new Block("Hey im the third block", secondBlock );
		Assertions.assertNotNull( thirdBlock );
		System.out.println("Hash for block 3 : " + thirdBlock.getHash() );
		Assertions.assertNotNull( thirdBlock.getHash() );
	}
	
	/**
	 * Test : blockchain
	 */
	@Test
	void testPart02() {
		List<Block> blockchain = BlockChain.build(); 
		// Add our blocks to the blockchain List:
		blockchain.add(new Block("Hi im the first block", "0"));		
		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).getHash() )); 
		blockchain.add(new Block("Hey im the third block",  blockchain.get(blockchain.size()-1).getHash() ));
		Assertions.assertEquals(3,  blockchain.size());
		
		String blockchainJson = JSONifiable.generateArray( blockchain ).toString(); 
		// String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
		
		// for (Block block : blockchain) { System.out.println( block ); }
	}
	
	@Test
	void testPart03pow() {
		List<Block> blockchain = BlockChain.build(); 
		int difficulty = 3;
		// Add our blocks to the blockchain List:
		blockchain.add(new Block("Hi im the first block", "0"));
		System.out.println("Trying to Mine block 1... ");
		ProofInterface ipi0 = new ProofOfWork(blockchain.get(0), difficulty);
		Assertions.assertNotNull(ipi0.proofTreatment());

		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).getHash() ));
		System.out.println("Trying to Mine block 2... ");
		ProofInterface ipi1 = new ProofOfWork(blockchain.get(1), difficulty);
		Assertions.assertNotNull(ipi1.proofTreatment());

		blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size()-1).getHash() ));
		System.out.println("Trying to Mine block 3... ");
		ProofInterface ipi2 = new ProofOfWork(blockchain.get(2), difficulty);
		Assertions.assertNotNull(ipi2.proofTreatment());
		
		Assertions.assertEquals(3,  blockchain.size());
		
		System.out.println("\nBlockchain is Valid: " + BlockChain.isChainValidV1( blockchain ));
		
		Assertions.assertTrue( BlockChain.isChainValidV1( blockchain ) );

		String blockchainJson = JSONifiable.generateArray( blockchain ).toString(); 
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
		
		// for (Block block : blockchain) { System.out.println( block ); }
	}
	
	@Test
	void testPart03pos() {
		List<Block> blockchain = BlockChain.build(); 
		int difficulty = 3;
		// Add our blocks to the blockchain List:
		blockchain.add(new Block("Hi im the first block", "0"));
		System.out.println("Trying to Mine block 1... ");
		ProofInterface ipi0 = new ProofOfStak(blockchain.get(0), difficulty);
		Assertions.assertNotNull(ipi0.proofTreatment());

		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).getHash() ));
		System.out.println("Trying to Mine block 2... ");
		ProofInterface ipi1 = new ProofOfStak(blockchain.get(1), difficulty);
		Assertions.assertNotNull(ipi1.proofTreatment());

		blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size()-1).getHash() ));
		System.out.println("Trying to Mine block 3... ");
		ProofInterface ipi2 = new ProofOfStak(blockchain.get(2), difficulty);
		Assertions.assertNotNull(ipi2.proofTreatment());
		
		Assertions.assertEquals(3,  blockchain.size());
		
		System.out.println("\nBlockchain is Valid: " + BlockChain.isChainValidV1( blockchain ));
		
		Assertions.assertTrue( BlockChain.isChainValidV1( blockchain ) );

		String blockchainJson = JSONifiable.generateArray( blockchain ).toString(); 
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
		
		// for (Block block : blockchain) { System.out.println( block ); }
	}
	
	@Test
	void testPart04() {
		// Setup Bouncy castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
		// Security.addProvider(new sun.security.provider.Sun());
		// XXX NOTE see https://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html
		// Create the new wallets
		Wallet walletA = new Wallet();
		Wallet walletB = new Wallet();
		Assertions.assertNotNull( walletA );
		Assertions.assertNotNull( walletB );
		// Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(StringUtils.getStringFromKey(walletA.getPrivateKey()));
		System.out.println(StringUtils.getStringFromKey(walletA.getPublicKey()));
		Assertions.assertNotNull( walletA.getPrivateKey() );
		Assertions.assertNotNull( walletA.getPublicKey() );
		System.out.println(StringUtils.getStringFromKey(walletB.getPrivateKey()));
		System.out.println(StringUtils.getStringFromKey(walletB.getPublicKey()));
		Assertions.assertNotNull( walletB.getPrivateKey() );
		Assertions.assertNotNull( walletB.getPublicKey() );
		// Create a test transaction from WalletA to walletB 
		Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
		Assertions.assertNotNull( transaction );
		transaction.generateSignature(walletA.getPrivateKey());
		Assertions.assertNotNull( walletA.getPrivateKey() );
		// Verify the signature works and verify it from the public key
		System.out.println("Is signature verified");
		System.out.println(transaction.verifySignature());
		Assertions.assertTrue( transaction.verifySignature() );
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
		Transaction genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f, null);
		Assertions.assertNotNull( genesisTransaction );
		// Manually sign the genesis transaction
		genesisTransaction.generateSignature(coinbase.getPrivateKey());
		// Manually set the transaction id
		genesisTransaction.setTransactionId( "0" );
		// Manually add the Transactions Output
		TransactionOutput trOut = new TransactionOutput(genesisTransaction.getRecipient(), 
														genesisTransaction.getValue(), 
														genesisTransaction.getTransactionId() );
		genesisTransaction.getOutputs().add( trOut );
		// Manually add the Transactions Input
		TransactionInput trInp = new TransactionInput(trOut.getId());
		trInp.setTransactionOutput( trOut );
		genesisTransaction.getInputs().add( trInp );
		
		// Its important to store our first transaction in the UTXOs list.
		TransactionOutput firstOuputTransaction = genesisTransaction.getOutputs().get(0);
		mapUTXOs.put(firstOuputTransaction.getId(), firstOuputTransaction);
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		Assertions.assertTrue( genesis.addTransaction(genesisTransaction, mapUTXOs, minimumTransaction) );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, genesis, new ProofOfWork(genesis, difficulty) ) );

		// Testing
		Block block1 = new Block(genesis.getHash());
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 100f, walletB, 000f, mapUTXOs) );
		
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		boolean bBlock1AddTransactionResult = block1.addTransaction(	walletA.sendFunds(walletB.getPublicKey(), 40f, mapUTXOs), 
																		mapUTXOs, minimumTransaction) ;
		Assertions.assertTrue( bBlock1AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block1, new ProofOfWork(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 60f, walletB, 40f, mapUTXOs) );
		
		boolean isChainValid = true; // BlockChain.isChainValidV1( blockchain );
		// Assertions.assertTrue( isChainValid );
		
		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block2 = new Block(block1.getHash());
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		boolean bBlock2AddTransactionResult = block2.addTransaction(	walletA.sendFunds(walletB.getPublicKey(), 1000f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		Assertions.assertFalse( bBlock2AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block2, new ProofOfWork(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 60, walletB, 40, mapUTXOs) );
		
		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block3 = new Block(block2.getHash());
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		boolean bBlock3AddTransactionResult = block3.addTransaction(	walletB.sendFunds( walletA.getPublicKey(), 20f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		
		Assertions.assertTrue( bBlock3AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block3, new ProofOfWork(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 80, walletB, 20, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block4 = new Block(block3.getHash());
		System.out.println("\nWalletA is Attempting to send funds (10) to WalletB...");
		boolean bBlock4AddTransactionResult = block4.addTransaction(	walletA.sendFunds( walletB.getPublicKey(), 10f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		
		Assertions.assertTrue( bBlock4AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block4, new ProofOfWork(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 70, walletB, 30, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block5 = new Block(block4.getHash());
		System.out.println("\nWalletB is Attempting to send funds (5) to WalletA...");
		boolean bBlock5AddTransactionResult = block5.addTransaction(	walletB.sendFunds( walletA.getPublicKey(), 05f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		
		Assertions.assertTrue( bBlock5AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block5, new ProofOfWork(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 75, walletB, 25, mapUTXOs) );

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
		Transaction genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f, null);
		Assertions.assertNotNull( genesisTransaction );
		// Manually sign the genesis transaction
		genesisTransaction.generateSignature(coinbase.getPrivateKey());
		// Manually set the transaction id
		genesisTransaction.setTransactionId( "0" );
		// Manually add the Transactions Output
		TransactionOutput trOut = new TransactionOutput(genesisTransaction.getRecipient(), 
														genesisTransaction.getValue(), 
														genesisTransaction.getTransactionId() );
		genesisTransaction.getOutputs().add( trOut );
		// Manually add the Transactions Input
		TransactionInput trInp = new TransactionInput(trOut.getId());
		trInp.setTransactionOutput( trOut );
		genesisTransaction.getInputs().add( trInp );
		
		// Its important to store our first transaction in the UTXOs list.
		TransactionOutput firstOuputTransaction = genesisTransaction.getOutputs().get(0);
		mapUTXOs.put(firstOuputTransaction.getId(), firstOuputTransaction);
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		Assertions.assertTrue( genesis.addTransaction(genesisTransaction, mapUTXOs, minimumTransaction) );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, genesis, new ProofOfStak(genesis, difficulty) ) );

		// Testing
		Block block1 = new Block(genesis.getHash());
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 100, walletB, 000, mapUTXOs) );
		
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		boolean bBlock1AddTransactionResult = block1.addTransaction(	walletA.sendFunds(walletB.getPublicKey(), 40f, mapUTXOs), 
																		mapUTXOs, minimumTransaction) ;
		Assertions.assertTrue( bBlock1AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block1, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 60, walletB, 40, mapUTXOs) );
		
		boolean isChainValid = true; // BlockChain.isChainValidV1( blockchain );
		// Assertions.assertTrue( isChainValid );
		
		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block2 = new Block(block1.getHash());
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		boolean bBlock2AddTransactionResult = block2.addTransaction(	walletA.sendFunds(walletB.getPublicKey(), 1000f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		Assertions.assertFalse( bBlock2AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block2, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 60, walletB, 40, mapUTXOs) );
		
		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block3 = new Block(block2.getHash());
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		boolean bBlock3AddTransactionResult = block3.addTransaction(	walletB.sendFunds( walletA.getPublicKey(), 20f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		
		Assertions.assertTrue( bBlock3AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block3, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 80, walletB, 20, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block4 = new Block(block3.getHash());
		System.out.println("\nWalletA is Attempting to send funds (10) to WalletB...");
		boolean bBlock4AddTransactionResult = block4.addTransaction(	walletA.sendFunds( walletB.getPublicKey(), 10f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		
		Assertions.assertTrue( bBlock4AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block4, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 70, walletB, 30, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block5 = new Block(block4.getHash());
		System.out.println("\nWalletB is Attempting to send funds (5) to WalletA...");
		boolean bBlock5AddTransactionResult = block5.addTransaction(	walletB.sendFunds( walletA.getPublicKey(), 05f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		
		Assertions.assertTrue( bBlock5AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block5, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 75, walletB, 25, mapUTXOs) );

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
		Transaction genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f, null);
		Assertions.assertNotNull( genesisTransaction );
		// Manually sign the genesis transaction
		genesisTransaction.generateSignature(coinbase.getPrivateKey());
		// Manually set the transaction id
		genesisTransaction.setTransactionId( "0" );
		// Manually add the Transactions Output
		TransactionOutput trOut = new TransactionOutput(genesisTransaction.getRecipient(), 
														genesisTransaction.getValue(), 
														genesisTransaction.getTransactionId() );
		genesisTransaction.getOutputs().add( trOut );
		// Manually add the Transactions Input
		TransactionInput trInp = new TransactionInput(trOut.getId());
		trInp.setTransactionOutput( trOut );
		genesisTransaction.getInputs().add( trInp );
		
		// Its important to store our first transaction in the UTXOs list.
		TransactionOutput firstOuputTransaction = genesisTransaction.getOutputs().get(0);
		mapUTXOs.put(firstOuputTransaction.getId(), firstOuputTransaction);
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		Assertions.assertTrue( genesis.addTransaction(genesisTransaction, mapUTXOs, minimumTransaction) );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, genesis, new ProofOfStak(genesis, difficulty) ) );

		// Testing
		Block block1 = new Block(genesis.getHash());
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 100, walletB, 000, mapUTXOs) );
		
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		boolean bBlock1AddTransactionResult = block1.addTransaction(	walletA.sendFunds(walletB.getPublicKey(), 40f, mapUTXOs), 
																		mapUTXOs, minimumTransaction) ;
		Assertions.assertTrue( bBlock1AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block1, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 60, walletB, 40, mapUTXOs) );
		
		boolean isChainValid = true; // BlockChain.isChainValidV1( blockchain );
		// Assertions.assertTrue( isChainValid );
		
		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block2 = new Block(block1.getHash());
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		boolean bBlock2AddTransactionResult = block2.addTransaction(	walletA.sendFunds(walletB.getPublicKey(), 1000f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		Assertions.assertFalse( bBlock2AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block2, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 60, walletB, 40, mapUTXOs) );
		
		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block3 = new Block(block2.getHash());
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		boolean bBlock3AddTransactionResult = block3.addTransaction(	walletB.sendFunds( walletA.getPublicKey(), 20f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		
		Assertions.assertTrue( bBlock3AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block3, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 80, walletB, 20, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block4 = new Block(block3.getHash());
		System.out.println("\nWalletA is Attempting to send funds (10) to WalletB...");
		boolean bBlock4AddTransactionResult = block4.addTransaction(	walletA.sendFunds( walletB.getPublicKey(), 10f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		
		Assertions.assertTrue( bBlock4AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block4, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 70, walletB, 30, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
		
		Block block5 = new Block(block4.getHash());
		System.out.println("\nWalletB is Attempting to send funds (5) to WalletA...");
		boolean bBlock5AddTransactionResult = block5.addTransaction(	walletB.sendFunds( walletA.getPublicKey(), 05f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		
		Assertions.assertTrue( bBlock5AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block5, new ProofOfStak(blockchain.get(blockchain.size()-1), difficulty) ) );
		Assertions.assertTrue(NoobChainFirstTests.checkBalances(walletA, 75, walletB, 25, mapUTXOs) );

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
	}
	
}
