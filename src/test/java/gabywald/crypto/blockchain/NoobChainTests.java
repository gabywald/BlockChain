package gabywald.crypto.blockchain;

import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// import com.google.gson.GsonBuilder;

import gabywald.global.json.JSONifiable;

/**
 * Tests about BlockChain / NoobChain. 
 * @author Gabriel Chandesris (2021)
 */
class NoobChainTests {

	/**
	 * Test : block. 
	 */
	@Test
	void testPart01() {
		Block genesisBlock = new Block("Hi im the first block", "0");
		Assertions.assertNotNull( genesisBlock );
		System.out.println("Hash for block 1 : " + genesisBlock.getHash() );
		Assertions.assertNotNull( genesisBlock.getHash() );
		
		Block secondBlock = new Block("Yo im the second block", genesisBlock.getHash() );
		Assertions.assertNotNull( secondBlock );
		System.out.println("Hash for block 2 : " + secondBlock.getHash() );
		Assertions.assertNotNull( secondBlock.getHash() );
		
		Block thirdBlock = new Block("Hey im the third block", secondBlock.getHash() );
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
	void testPart03() {
		List<Block> blockchain = BlockChain.build(); 
		int difficulty = 3;
		// Add our blocks to the blockchain List:
		blockchain.add(new Block("Hi im the first block", "0"));
		System.out.println("Trying to Mine block 1... ");
		blockchain.get(0).mineBlock(difficulty);

		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).getHash() ));
		System.out.println("Trying to Mine block 2... ");
		blockchain.get(1).mineBlock(difficulty);

		blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size()-1).getHash() ));
		System.out.println("Trying to Mine block 3... ");
		blockchain.get(2).mineBlock(difficulty);
		
		Assertions.assertEquals(3,  blockchain.size());
		
		System.out.println("\nBlockchain is Valid: " + BlockChain.isChainValidV1( blockchain ));
		
		Assertions.assertTrue( BlockChain.isChainValidV1( blockchain ) );

		String blockchainJson = JSONifiable.generateArray( blockchain ).toString(); 
		// String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
		
		// for (Block block : blockchain) { System.out.println( block ); }
	}
	
	@Test
	void testPart04() {
		// Setup Bouncey castle as a Security Provider
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
		Assertions.assertNotNull( walletB.getPrivateKey() );
		Assertions.assertNotNull( walletB.getPublicKey() );
		//Create a test transaction from WalletA to walletB 
		Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
		Assertions.assertNotNull( transaction );
		transaction.generateSignature(walletA.getPrivateKey());
		Assertions.assertNotNull( walletA.getPrivateKey() );
		//Verify the signature works and verify it from the public key
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
		
		// Setup Bouncey castle as a Security Provider
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
		genesisTransaction.getOutputs().add(new TransactionOutput(	genesisTransaction.getRecipient(), 
																	genesisTransaction.getValue(), 
																	genesisTransaction.getTransactionId() ));
		
		// Its important to store our first transaction in the UTXOs list.
		mapUTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction, mapUTXOs, minimumTransaction);
		BlockChain.addBlock(blockchain, genesis, difficulty);

		// Testing
		Block block1 = new Block(genesis.getHash() );
		System.out.println("\nWalletA's balance is: " + walletA.getBalance( mapUTXOs ));
		
		Assertions.assertEquals(100, walletA.getBalance( mapUTXOs ));
		
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		boolean bBlock1AddTransactionResult = block1.addTransaction(	walletA.sendFunds(walletB.getPublicKey(), 40f, mapUTXOs), 
																		mapUTXOs, minimumTransaction) ;
		Assertions.assertTrue( bBlock1AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block1, difficulty) );
		System.out.println("\nWalletA's balance is: " + walletA.getBalance( mapUTXOs ));
		System.out.println("WalletB's balance is: " + walletB.getBalance( mapUTXOs ));
		
		Assertions.assertEquals(60, walletA.getBalance( mapUTXOs ));
		Assertions.assertEquals(40, walletB.getBalance( mapUTXOs ));
		
		boolean isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block2 = new Block(block1.getHash() );
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		boolean bBlock2AddTransactionResult = block2.addTransaction(	walletA.sendFunds(walletB.getPublicKey(), 1000f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		Assertions.assertFalse( bBlock2AddTransactionResult);
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block2, difficulty) );
		System.out.println("\nWalletA's balance is: " + walletA.getBalance( mapUTXOs ));
		System.out.println("WalletB's balance is: " + walletB.getBalance( mapUTXOs ));
		
		Assertions.assertEquals(60, walletA.getBalance( mapUTXOs ));
		Assertions.assertEquals(40, walletB.getBalance( mapUTXOs ));
		
		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block3 = new Block(block2.getHash() );
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		boolean bBlock3AddTransactionResult = block3.addTransaction(	walletB.sendFunds( walletA.getPublicKey(), 20f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		
		Assertions.assertTrue( bBlock3AddTransactionResult );
		System.out.println("\nWalletA's balance is: " + walletA.getBalance( mapUTXOs ));
		System.out.println("WalletB's balance is: " + walletB.getBalance( mapUTXOs ));
		
		Assertions.assertEquals(80, walletA.getBalance( mapUTXOs ));
		Assertions.assertEquals(20, walletB.getBalance( mapUTXOs ));

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
	}

}
