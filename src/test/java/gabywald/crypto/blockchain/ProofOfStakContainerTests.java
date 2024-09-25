package gabywald.crypto.blockchain;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

class ProofOfStakContainerTests {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testSelectValidator() {
		
		Logger.setLogLevel(LoggerLevel.LL_WARNING);
		
		ProofOfStakContainer posc = ProofOfStakContainer.getInstance();
		
		Assertions.assertNull( posc.selectValidator() );
		
		Assertions.assertTrue( posc.addValidator(100, 10) );
		Assertions.assertTrue( posc.addValidator(200, 20) );
		Assertions.assertTrue( posc.addValidator(150, 30) );

		Assertions.assertFalse( posc.addValidator(-10, 30) );
		Assertions.assertFalse( posc.addValidator(150, -3) );
		
		Assertions.assertEquals(3, posc.size());
		
		Assertions.assertNotNull( posc.selectValidator() );
	}

}
