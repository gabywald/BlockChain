package gabywald.crypto.blockchain;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * Tests about BlockChain / NoobChain : Proof Of Stack. 
 * @author Gabriel Chandesris (2024)
 */
class NoobChainSimplePOSTests {
	
	@Test
	void testPartPoSsimple() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		BlockChain blockchain = BlockChain.build();
		TransactionOutputsContainer mapUTXOs = new TransactionOutputsContainer();
		int difficulty = ProofOfStak.STAK_DIFFICULTY;
		float minimumTransaction = 0.1f;
		Class<? extends ProofInterface> iProofClass = ProofOfStak.class;
		
		// Setup Bouncy castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		// Base Coin Wallet !
		Wallet coinbase = new Wallet( "coinbase" );
		// Create wallets:
		Wallet walletA = new Wallet( "A" );
		Wallet walletB = new Wallet( "B" );
		
		// Wallet for staking : initalization
		float initStakers = 30f;
		Wallet wallet1 = new Wallet( "1" );
		Wallet wallet2 = new Wallet( "2" );
		Wallet wallet3 = new Wallet( "3" );
		// TODO genesis transaction with initialization
		// Create genesis transaction, which sends 'startCoins' NoobCoin to current wallet: 
		Transaction genesisTransaction01 = new Transaction(coinbase.getPublicKey(), wallet1.getPublicKey(), initStakers, null);
		Transaction genesisTransaction02 = new Transaction(coinbase.getPublicKey(), wallet2.getPublicKey(), initStakers, null);
		Transaction genesisTransaction03 = new Transaction(coinbase.getPublicKey(), wallet3.getPublicKey(), initStakers, null);
		Transaction genesisTransaction0A = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f, null);
		// Manually sign the genesis transaction
		genesisTransaction01.generateSignature(coinbase.getPrivateKey());
		genesisTransaction02.generateSignature(coinbase.getPrivateKey());
		genesisTransaction03.generateSignature(coinbase.getPrivateKey());
		genesisTransaction0A.generateSignature(coinbase.getPrivateKey());
		// Manually set the transaction id
		genesisTransaction01.setTransactionId( Wallet.GENESIS_TRANSACTION_ID );
		genesisTransaction02.setTransactionId( Wallet.GENESIS_TRANSACTION_ID );
		genesisTransaction03.setTransactionId( Wallet.GENESIS_TRANSACTION_ID );
		genesisTransaction0A.setTransactionId( Wallet.GENESIS_TRANSACTION_ID );
		// Manually add the Transactions Output
		TransactionOutput trOut01 = new TransactionOutput(genesisTransaction01.getRecipient(), genesisTransaction01.getValue(), genesisTransaction01.getTransactionId() );
		TransactionOutput trOut02 = new TransactionOutput(genesisTransaction02.getRecipient(), genesisTransaction02.getValue(), genesisTransaction02.getTransactionId() );
		TransactionOutput trOut03 = new TransactionOutput(genesisTransaction03.getRecipient(), genesisTransaction03.getValue(), genesisTransaction03.getTransactionId() );
		TransactionOutput trOut0A = new TransactionOutput(genesisTransaction0A.getRecipient(), genesisTransaction0A.getValue(), genesisTransaction0A.getTransactionId() );
		genesisTransaction01.getOutputs().add( trOut01 );
		genesisTransaction02.getOutputs().add( trOut02 );
		genesisTransaction03.getOutputs().add( trOut03 );
		genesisTransaction0A.getOutputs().add( trOut0A );
		// Manually add the Transactions Input
		TransactionInput trInp01 = new TransactionInput( trOut01 );
		TransactionInput trInp02 = new TransactionInput( trOut02 );
		TransactionInput trInp03 = new TransactionInput( trOut03 );
		TransactionInput trInp0A = new TransactionInput( trOut0A );
		genesisTransaction01.getInputs().add( trInp01 );
		genesisTransaction02.getInputs().add( trInp02 );
		genesisTransaction03.getInputs().add( trInp03 );
		genesisTransaction0A.getInputs().add( trInp0A );
		// Its important to store our first transaction in the UTXOs list.
		mapUTXOs.put(genesisTransaction01.getOutputs().get(0).getId(), genesisTransaction01.getOutputs().get(0));
		mapUTXOs.put(genesisTransaction02.getOutputs().get(0).getId(), genesisTransaction02.getOutputs().get(0));
		mapUTXOs.put(genesisTransaction03.getOutputs().get(0).getId(), genesisTransaction03.getOutputs().get(0));
		mapUTXOs.put(genesisTransaction0A.getOutputs().get(0).getId(), genesisTransaction0A.getOutputs().get(0));
		
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Creating and Mining Genesis block... ");
		Block genesis = new Block( genesisTransaction01.getTransactionId() ); // "0"
		genesis.addTransaction(genesisTransaction01, mapUTXOs, minimumTransaction);
		genesis.addTransaction(genesisTransaction02, mapUTXOs, minimumTransaction);
		genesis.addTransaction(genesisTransaction03, mapUTXOs, minimumTransaction);
		genesis.addTransaction(genesisTransaction0A, mapUTXOs, minimumTransaction);
		try {
			Constructor<?> cons = ProofBasic.class.getConstructor(Block.class, Integer.class);
			BlockChain.addBlock(blockchain, (ProofInterface)cons.newInstance(genesis, difficulty) );
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{ e.printStackTrace(); }
		
		Assertions.assertNotNull( genesis );
		Assertions.assertNotNull( genesis.getTransactions() );
		Assertions.assertNotNull( genesis.getTransactions().get(0) );
		Assertions.assertEquals(4, genesis.getTransactions().size() );
		for (Transaction tr : genesis.getTransactions()) 
			{ Assertions.assertNotNull( tr ); }
		
		Assertions.assertTrue( TransactionOutputsContainer.checkBalance(wallet1, initStakers, mapUTXOs) );
		Assertions.assertTrue( TransactionOutputsContainer.checkBalance(wallet2, initStakers, mapUTXOs) );
		Assertions.assertTrue( TransactionOutputsContainer.checkBalance(wallet3, initStakers, mapUTXOs) );
		Assertions.assertTrue( TransactionOutputsContainer.checkBalance(walletA, 100f, mapUTXOs) );
		
		// Wallet for staking : adding in stak container
		ProofOfStakContainer stakContainer = ProofOfStakContainer.getInstance();
		stakContainer.addValidator(10, 10, wallet1.getPublicKey());
		stakContainer.addValidator(10, 10, wallet1.getPublicKey());
		stakContainer.addValidator(10, 10, wallet1.getPublicKey());
		stakContainer.addValidator(20, 20, wallet2.getPublicKey());
		stakContainer.addValidator(10, 10, wallet2.getPublicKey());
		stakContainer.addValidator(30, 30, wallet3.getPublicKey());
		Assertions.assertEquals(6, stakContainer.size());
		
		Assertions.assertTrue( TransactionOutputsContainer.checkBalance(walletA, 100f, mapUTXOs) );
		Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 100f, walletB, 000f, mapUTXOs) );

		// ***** Real Transactions on A and B start here ! *****
		// Testing: A send 40 to B
		Block lastBlock = walletA.nextTransaction(40f, minimumTransaction, genesis, walletB, blockchain, difficulty, mapUTXOs, iProofClass);
		Assertions.assertNotNull( lastBlock );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction0A, difficulty ) );
		Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 60f, walletB, 40f, mapUTXOs) );

		// Testing: A send 1000 to B (not working !)
		lastBlock = walletA.nextTransaction(1000f, minimumTransaction, lastBlock, walletB, blockchain, difficulty, mapUTXOs, iProofClass);
		Assertions.assertNotNull( lastBlock );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction0A, difficulty ) );
		Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 60f, walletB, 40f, mapUTXOs) );

		// Testing: B send 20 to A
		lastBlock = walletB.nextTransaction(20f, minimumTransaction, lastBlock, walletA, blockchain, difficulty, mapUTXOs, iProofClass);
		Assertions.assertNotNull( lastBlock );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction0A, difficulty ) );
		// XXX BUG Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 80f, walletB, 20f, mapUTXOs) );
		
		// Testing: A send 10 to B
		lastBlock = walletA.nextTransaction(10f, minimumTransaction, lastBlock, walletB, blockchain, difficulty, mapUTXOs, iProofClass);
		Assertions.assertNotNull( lastBlock );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction0A, difficulty ) );
		// XXX BUG Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 70f, walletB, 30f, mapUTXOs) );
		
		// Testing: B send 5 to A
		lastBlock = walletB.nextTransaction( 5f, minimumTransaction, lastBlock, walletA, blockchain, difficulty, mapUTXOs, iProofClass);
		Assertions.assertNotNull( lastBlock );
		Assertions.assertTrue( BlockChain.isChainValidV2( blockchain, genesisTransaction0A, difficulty ) );
		// XXX BUG Assertions.assertTrue( TransactionOutputsContainer.checkBalances(walletA, 75f, walletB, 25f, mapUTXOs) );
	}

}
