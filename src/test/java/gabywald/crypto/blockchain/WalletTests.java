package gabywald.crypto.blockchain;

import java.security.Security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * Tests about BlockChain / NoobChain. 
 * @author Gabriel Chandesris (2024)
 */
class WalletTests {

	@BeforeEach
	void setUp() throws Exception {
		// Setup Bouncy castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testWallet() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		Wallet wallet = new Wallet( "test" );
		Assertions.assertNotNull(wallet);
		Assertions.assertNotNull(StringUtils.getStringFromKey(wallet.getPrivateKey()));
		Assertions.assertNotNull(StringUtils.getStringFromKey(wallet.getPublicKey()));
	}

	@Test
	void testCreateGenesisTransactionBlock() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		BlockChain blockchain = BlockChain.build();
		TransactionOutputsContainer mapUTXOs = new TransactionOutputsContainer();
		int difficulty = ProofBasic.BASIC_DIFFICULTY;
		float minimumTransaction = 0.1f;
		// Create wallets:
		Wallet walletA = new Wallet( "A" );
		Wallet coinbase = new Wallet( "coinbase" );
		// Create genesis transaction/block, which sends 100 NoobCoin to walletA: 
		Block genesis = walletA.createGenesisTransaction(100f, minimumTransaction, ProofBasic.class, blockchain, difficulty, coinbase, mapUTXOs );
		Assertions.assertNotNull( genesis );
		Assertions.assertTrue( TransactionOutputsContainer.checkBalance(walletA, 100f, mapUTXOs) );
	}
	
	@Test
	void testSendFunds() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		BlockChain blockchain = BlockChain.build();
		TransactionOutputsContainer mapUTXOs = new TransactionOutputsContainer();
		int difficulty = ProofBasic.BASIC_DIFFICULTY;
		float minimumTransaction = 0.1f;
		
		// Setup Bouncy castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Create wallets:
		Wallet walletA = new Wallet( "A" );
		Wallet walletB = new Wallet( "B" );
		Wallet coinbase = new Wallet( "coinbase" );

		// Create genesis transaction/block, which sends 100 NoobCoin to walletA: 
		Block genesis = walletA.createGenesisTransaction(100f, minimumTransaction, ProofBasic.class, 
														 blockchain, difficulty, coinbase, mapUTXOs );
		Assertions.assertNotNull( genesis );
		Assertions.assertNotNull( genesis.getTransactions() );
		Assertions.assertNotNull( genesis.getTransactions().get(0) );
		Transaction genesisTransaction = genesis.getTransactions().get(0);
		Assertions.assertNotNull( genesisTransaction );
		// Assertions.assertTrue( TransactionOutputsContainer.checkBalance(walletA, 100f, mapUTXOs) );

		// Testing
		Block block1 = walletA.nextTransaction(5f, minimumTransaction, genesis, walletB, 
											   blockchain, difficulty, mapUTXOs, ProofBasic.class);
		Assertions.assertNotNull( block1 );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
		// Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 95f, walletB, 5f, mapUTXOs) );
		
		// Testing
		Block block2 = walletA.nextTransaction(5f, minimumTransaction, block1, walletB, 
											   blockchain, difficulty, mapUTXOs, ProofBasic.class);
		Assertions.assertNotNull( block2 );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty ) );
		// Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 90f, walletB, 10f, mapUTXOs) );
	}
	
	@Test
	void testGetBalance() {
		// TODO fail("Not yet implemented");
	}

}
