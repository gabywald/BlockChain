package gabywald.crypto.blockchain;

import java.security.Security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * Tests about BlockChain / NoobChain. 
 * @author Gabriel Chandesris (2021, 2023, 2024)
 */
class NoobChainFirstTests {
	
	/**
	 * Test : block. 
	 */
	@Test
	void testPartBlocks() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		Block genesisBlock = new Block("Hi im the first block", Wallet.GENESIS_TRANSACTION_ID);
		Assertions.assertNotNull( genesisBlock );
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Hash for block 1 : " + genesisBlock.getHash() );
		Assertions.assertNotNull( genesisBlock.getHash() );
		
		Block secondBlock = new Block("Yo im the second block", genesisBlock );
		Assertions.assertNotNull( secondBlock );
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Hash for block 2 : " + secondBlock.getHash() );
		Assertions.assertNotNull( secondBlock.getHash() );
		
		Block thirdBlock = new Block("Hey im the third block", secondBlock );
		Assertions.assertNotNull( thirdBlock );
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Hash for block 3 : " + thirdBlock.getHash() );
		Assertions.assertNotNull( thirdBlock.getHash() );
	}
	
	/**
	 * Test : blockchain
	 */
	@Test
	void testPartBlockChain() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		BlockChain blockchain = BlockChain.build(); 
		// Add our blocks to the blockchain List:
		blockchain.add(new Block("Hi im the first block", Wallet.GENESIS_TRANSACTION_ID));		
		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).getHash() )); 
		blockchain.add(new Block("Hey im the third block",  blockchain.get(blockchain.size()-1).getHash() ));
		Assertions.assertEquals(3,  blockchain.size());
		
		String blockchainJson = blockchain.toString(); 
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "\nThe block chain: ");
		Logger.printlnLog(LoggerLevel.LL_FORUSER, blockchainJson);
	}
	
	@Test
	void testPartPOWbase() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		BlockChain blockchain = BlockChain.build(); 
		int difficulty = 3;
		// Add our blocks to the blockchain List:
		blockchain.add(new Block("Hi im the first block", Wallet.GENESIS_TRANSACTION_ID));
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Trying to Mine block 1... ");
		ProofInterface ipi0 = new ProofOfWork(blockchain.get(0), difficulty);
		Assertions.assertNotNull(ipi0.proofTreatment());

		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).getHash() ));
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Trying to Mine block 2... ");
		ProofInterface ipi1 = new ProofOfWork(blockchain.get(1), difficulty);
		Assertions.assertNotNull(ipi1.proofTreatment());

		blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size()-1).getHash() ));
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Trying to Mine block 3... ");
		ProofInterface ipi2 = new ProofOfWork(blockchain.get(2), difficulty);
		Assertions.assertNotNull(ipi2.proofTreatment());
		
		Assertions.assertEquals(3,  blockchain.size());
		
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "\nBlockchain is Valid: " + BlockChain.isChainValidV1( blockchain ));
		
		Assertions.assertTrue( BlockChain.isChainValidV1( blockchain ) );

		String blockchainJson = blockchain.toString(); 
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "\nThe block chain: ");
		Logger.printlnLog(LoggerLevel.LL_FORUSER, blockchainJson);
	}
	
	@Test
	void testPartPOSbase() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		BlockChain blockchain = BlockChain.build(); 
		int difficulty = 3;
		// Add our blocks to the blockchain List:
		blockchain.add(new Block("Hi im the first block", Wallet.GENESIS_TRANSACTION_ID));
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Trying to Mine block 1... ");
		ProofInterface ipi0 = new ProofOfStak(blockchain.get(0), difficulty);
		Assertions.assertNotNull(ipi0.proofTreatment());

		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).getHash() ));
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Trying to Mine block 2... ");
		ProofInterface ipi1 = new ProofOfStak(blockchain.get(1), difficulty);
		Assertions.assertNotNull(ipi1.proofTreatment());

		blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size()-1).getHash() ));
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Trying to Mine block 3... ");
		ProofInterface ipi2 = new ProofOfStak(blockchain.get(2), difficulty);
		Assertions.assertNotNull(ipi2.proofTreatment());
		
		Assertions.assertEquals(3,  blockchain.size());
		
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "\nBlockchain is Valid: " + BlockChain.isChainValidV1( blockchain ));
		
		Assertions.assertTrue( BlockChain.isChainValidV1( blockchain ) );

		String blockchainJson = blockchain.toString();
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "\nThe block chain: ");
		Logger.printlnLog(LoggerLevel.LL_FORUSER, blockchainJson);
	}
	
	@Test
	void testPartSecurity() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		// Setup Bouncy castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
		// Security.addProvider(new sun.security.provider.Sun());
		// XXX NOTE see https://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html
		// Create the new wallets
		Wallet walletA = new Wallet( "A" );
		Wallet walletB = new Wallet( "B" );
		Assertions.assertNotNull( walletA );
		Assertions.assertNotNull( walletB );
		// Test public and private keys
		// // Logger.printlnLog(LoggerLevel.LL_FORUSER, "Private and public keys:");
		Logger.printlnLog(LoggerLevel.LL_FORUSER, StringUtils.getStringFromKey(walletA.getPrivateKey()));
		Logger.printlnLog(LoggerLevel.LL_FORUSER, StringUtils.getStringFromKey(walletA.getPublicKey()));
		Assertions.assertNotNull( walletA.getPrivateKey() );
		Assertions.assertNotNull( walletA.getPublicKey() );
		Logger.printlnLog(LoggerLevel.LL_FORUSER, StringUtils.getStringFromKey(walletB.getPrivateKey()));
		Logger.printlnLog(LoggerLevel.LL_FORUSER, StringUtils.getStringFromKey(walletB.getPublicKey()));
		Assertions.assertNotNull( walletB.getPrivateKey() );
		Assertions.assertNotNull( walletB.getPublicKey() );
		// Create a test transaction from WalletA to walletB 
		Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
		Assertions.assertNotNull( transaction );
		transaction.generateSignature(walletA.getPrivateKey());
		Assertions.assertNotNull( walletA.getPrivateKey() );
		// Verify the signature works and verify it from the public key
		Logger.printlnLog(LoggerLevel.LL_FORUSER, "Is signature verified");
		Logger.printlnLog(LoggerLevel.LL_FORUSER, transaction.verifySignature() + "");
		Assertions.assertTrue( transaction.verifySignature() );
	}
	
}
