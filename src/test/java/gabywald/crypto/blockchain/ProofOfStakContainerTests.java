package gabywald.crypto.blockchain;


import java.security.Security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

class ProofOfStakContainerTests {

	@BeforeEach
	void setUp() throws Exception {
		ProofOfStakContainer.getInstance().clear();
	}

	@AfterEach
	void tearDown() throws Exception {
		ProofOfStakContainer.getInstance().clear();
	}

	@Test
	void testSelectValidator() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		ProofOfStakContainer posc = ProofOfStakContainer.getInstance();
		
		Assertions.assertNull( posc.selectValidator() );
		
		// Basic Elements for Validators ... 
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		BlockChain blockchain = BlockChain.build();
		TransactionOutputsContainer mapUTXOs = new TransactionOutputsContainer();
		int difficulty = ProofOfStak.STAK_DIFFICULTY;
		float minimumTransaction = 0.1f;
		Class<? extends ProofInterface> iProofClass = ProofOfStak.class;
		Wallet coinbase = new Wallet( "coinbase" );
		Wallet wallet1 = new Wallet( "1" );
		Block genesis1 = wallet1.createGenesisTransaction(1000f, minimumTransaction, iProofClass, blockchain, difficulty, coinbase, mapUTXOs );
		
		// Tests begin  here ! *****
		Assertions.assertTrue( posc.addValidator(100, 10, wallet1.getPublicKey()) );
		Assertions.assertTrue( posc.addValidator(200, 20, wallet1.getPublicKey()) );
		Assertions.assertTrue( posc.addValidator(150, 30, wallet1.getPublicKey()) );

		Assertions.assertFalse( posc.addValidator(-10, 30, wallet1.getPublicKey()) );
		Assertions.assertFalse( posc.addValidator(150, -3, wallet1.getPublicKey()) );
		
		// TODO Assertions.assertFalse( posc.addValidator(3000f, 10, wallet1.getPublicKey()) );
		
		Assertions.assertEquals(3, posc.size());
		
		Assertions.assertNotNull( posc.selectValidator() );
	}

}
