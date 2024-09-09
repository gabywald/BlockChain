package gabywald.crypto.blockchain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests about BlockChain / NoobChain. 
 * @author Gabriel Chandesris (2024)
 */
class TransactionOutputsContainerTests {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGlobal() {
		TransactionOutputsContainer toc = new TransactionOutputsContainer();
		Assertions.assertNotNull( toc );
		Assertions.assertNull( toc.getTransactionOutput( "key" ) );
		
		toc.put("id", null);
		Assertions.assertNull( toc.getTransactionOutput( "id" ) );
		
		try {
			KeyPairGenerator keyGen01 = KeyPairGenerator.getInstance( "DSA" );
			KeyPair keyPair01 = keyGen01.generateKeyPair();
			
			TransactionOutput to = new TransactionOutput(keyPair01.getPublic(), 42f, "0");
			toc.put(to.getId(), to);
			Assertions.assertNotNull( toc.getTransactionOutput( to.getId() ) );
			
			Assertions.assertEquals(1, toc.size());
			
			String id2remove = to.getId();

			IntStream.range(0, 100).forEach( i -> {
				TransactionOutput toNext = new TransactionOutput(keyPair01.getPublic(), 42f, "" + i);
				toc.put(toNext.getId(), toNext);
				Assertions.assertNotNull( toc.getTransactionOutput( toNext.getId() ) );
			});
			
			Assertions.assertEquals(100, toc.size() );
			Assertions.assertEquals(42*100, toc.getBalance(keyPair01.getPublic()) );
			
			toc.remove(id2remove);
			
			Assertions.assertEquals(99, toc.size() );
			Assertions.assertEquals((42)*99, toc.getBalance(keyPair01.getPublic()) );
		} catch (NoSuchAlgorithmException e) 
			{ Assertions.fail( e.getMessage() ); }
		
	}
	
	@Test
	void testMixer() {
		TransactionOutputsContainer toc = new TransactionOutputsContainer();
		Assertions.assertNotNull( toc );
		Assertions.assertNull( toc.getTransactionOutput( "key" ) );
		
		toc.put("id", null);
		Assertions.assertNull( toc.getTransactionOutput( "id" ) );
		
		try {
			KeyPairGenerator keyGen01 = KeyPairGenerator.getInstance( "DSA" );
			KeyPair keyPair01 = keyGen01.generateKeyPair();
			
			KeyPairGenerator keyGen02 = KeyPairGenerator.getInstance( "DSA" );
			KeyPair keyPair02 = keyGen02.generateKeyPair();
			
			KeyPairGenerator keyGen03 = KeyPairGenerator.getInstance( "DSA" );
			KeyPair keyPair03 = keyGen03.generateKeyPair();
			
			List<String> to2remove = new ArrayList<String>();
			Arrays.asList(keyPair01, keyPair02, keyPair03).stream().forEach( kp -> {
				TransactionOutput to = new TransactionOutput(kp.getPublic(), 42f, "0");
				toc.put(to.getId(), to);
				Assertions.assertNotNull( toc.getTransactionOutput( to.getId() ) );
				to2remove.add( to.getId() );
			});
			Assertions.assertEquals(3, toc.size());
			
			Arrays.asList(keyPair01, keyPair02, keyPair03).stream().forEach( kp -> {
				IntStream.range(0, 100).forEach( i -> {
					TransactionOutput toNext = new TransactionOutput(kp.getPublic(), 42f, "" + i);
					toc.put(toNext.getId(), toNext);
					Assertions.assertNotNull( toc.getTransactionOutput( toNext.getId() ) );
				});
			});
			
			Assertions.assertEquals(300, toc.size() );
			Assertions.assertEquals(42*100, toc.getBalance(keyPair01.getPublic()) );
			Arrays.asList(keyPair01, keyPair02, keyPair03).stream()
				.forEach( kp -> { Assertions.assertEquals(42*100, toc.getBalance(kp.getPublic()) ); });
			
			to2remove.stream().forEach( id2remove -> toc.remove(id2remove) );
			
			Assertions.assertEquals(300-3, toc.size() );
			Assertions.assertEquals((42)*99, toc.getBalance(keyPair01.getPublic()) );
			
			Arrays.asList(keyPair01, keyPair02, keyPair03).stream()
				.forEach( kp -> { Assertions.assertEquals(42*99, toc.getBalance(kp.getPublic()) ); });
			
		} catch (NoSuchAlgorithmException e) 
			{ Assertions.fail( e.getMessage() ); }
		
	}

}
