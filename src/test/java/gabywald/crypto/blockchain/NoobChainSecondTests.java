package gabywald.crypto.blockchain;

import java.security.Security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * Tests about BlockChain / NoobChain. 
 * @author Gabriel Chandesris (2024)
 */
class NoobChainSecondTests {
	
	@Test
	void testPartPoWSample() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
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
		Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 60f, walletB, 40f, mapUTXOs) );

	}
	
	@Test
	void testPartPoSsample() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		BlockChain blockchain = BlockChain.build();
		TransactionOutputsContainer mapUTXOs = new TransactionOutputsContainer();
		int difficulty = ProofOfStak.STAK_DIFFICULTY;
		float minimumTransaction = 0.1f;
		Class<? extends ProofInterface> iProofClass = ProofOfStak.class;
		
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
		Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 60f, walletB, 40f, mapUTXOs) );

	}

	@Test
	void testPartPoSnext() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		// TODO add here a walletC to give stak for transferts between A and B !!
		
		BlockChain blockchain = BlockChain.build();
		TransactionOutputsContainer mapUTXOs = new TransactionOutputsContainer();
		int difficulty = ProofOfStak.STAK_DIFFICULTY;
		float minimumTransaction = 0.1f;
		Class<? extends ProofInterface> iProofClass = ProofOfStak.class;
		
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
		Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 60f, walletB, 40f, mapUTXOs) );

	}
	
}
