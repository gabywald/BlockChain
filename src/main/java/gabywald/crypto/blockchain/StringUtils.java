package gabywald.crypto.blockchain;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;

/**
 * StringUtil of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa">https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-1">https://github.com/CryptoKass/NoobChain-Tutorial-Part-1</a>
 * <br/><a href="https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce">https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-2">https://github.com/CryptoKass/NoobChain-Tutorial-Part-2</a>
 * @author Gabriel Chandesris (2021)
 */
public abstract class StringUtils {

	/** 
	 * Applies Sha256 to a string and returns the result. 
	 * @param input
	 * @return (String)
	 */
	public static String applySha256(String input)  {		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			// Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			// This will contain hash as hexidecimal
			StringBuffer hexString = new StringBuffer();
			for (int i = 0 ; i < hash.length ; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			System.out.println( "StringUtils: " + e.getClass().getName() + ": " + e.getMessage() );
			// throw new BlockchainException("StringUtils: " + e.getClass().getName() +": " + e.getMessage());
			return null;
		}
	}

	public static final String ECDSA = "ECDSA"; // CipherAlgorithm
	public static final String BC = "BC"; // CipherProvider

	//	public static final String CipherAlgorithm = "DiffieHellman"; // "SHA1withDSA";
	//	public static final String CipherProvider = "SunJCE"; // "SUN";

	// XXX NOTE see https://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html
	// TODO learning how security works here ! 

	/**
	 * Applies ECDSA Signature and returns the result ( as bytes ). 
	 * @param privateKey
	 * @param input
	 * @return (byte[])
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws SignatureException 
	 */
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) 
			throws BlockchainException {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance(StringUtils.ECDSA, StringUtils.BC);
			// dsa = Signature.getInstance(StringUtils.CipherAlgorithm, StringUtils.CipherProvider);
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException  | SignatureException e) {
			System.out.println( "StringUtils: " + e.getClass().getName() + ": " + e.getMessage() );
			throw new BlockchainException("StringUtils: " + e.getClass().getName() + ": " + e.getMessage());
		}
		return output;
	}

	/**
	 * Verifies a String signature. 
	 * @param publicKey
	 * @param data
	 * @param signature
	 * @return (boolean)
	 */
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance(StringUtils.ECDSA, StringUtils.BC);
			// Signature ecdsaVerify = Signature.getInstance(StringUtils.CipherAlgorithm, StringUtils.CipherProvider);
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException  | SignatureException e) {
			// throw new BlockchainException("StringUtils: " + e.getClass().getName() + ": " + e.getMessage());
			System.out.println( "StringUtils: " + e.getClass().getName() + ": " + e.getMessage() );
		}
		return false; // if any Exception is throwed !
	}

	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	/**
	 * Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"  
	 * @param difficulty
	 * @return (String)
	 */
	public static String getDifficultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}

	/** 
	 * Tacks in array of transactions and returns a merkle root.
	 * @param transactions
	 * @return (String)
	 */
	public static String getMerkleRoot(List<Transaction> transactions) {
		int count = transactions.size();
		List<String> previousTreeLayer = new ArrayList<String>();
		for (Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.getTransactionId());
		}
		List<String> treeLayer = previousTreeLayer;
		while (count > 1) {
			treeLayer = new ArrayList<String>();
			for (int i = 1 ; i < previousTreeLayer.size() ; i++) {
				String calculedHash = StringUtils.applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i));
				if (calculedHash != null) 
					{ treeLayer.add( calculedHash ); }
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}

}

