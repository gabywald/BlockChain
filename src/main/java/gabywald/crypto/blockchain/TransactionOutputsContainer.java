package gabywald.crypto.blockchain;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import gabywald.global.json.JSONException;
import gabywald.global.json.JSONifiable;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

import java.util.Set;

/**
 * UTXOs : Unspent Transactions Output 
 * @author Gabriel Chandesris (2024)
 */
public class TransactionOutputsContainer extends JSONifiable {

	private Map<String, TransactionOutput> mapUTXOs = new HashMap<String, TransactionOutput>();
	
	private List<String> datalog = new ArrayList<String>();

	public void put(String id, TransactionOutput outputTransaction) {
		if (outputTransaction == null) { return; }
		String log2add = "PUT {" + outputTransaction.getValue() + "} (" + this.mapUTXOs.size() + ") [" + id + "]";
		this.datalog.add( log2add );
		Logger.printlnLog(LoggerLevel.LL_DEBUG, log2add );
		this.mapUTXOs.put(id, outputTransaction);
	}
	
	public TransactionOutput getTransactionOutput(String transactionOutputId) {
		if (this.mapUTXOs.containsKey(transactionOutputId)) 
			{ return this.mapUTXOs.get(transactionOutputId); }
		return null;
	}
	
	public void remove(String transactionOutputId) {
		if (this.mapUTXOs.containsKey(transactionOutputId)) {
			String log2add = "REM {" + this.mapUTXOs.get(transactionOutputId).getValue() + "} (" + this.mapUTXOs.size() + ") [" + transactionOutputId + "]";
			this.datalog.add( log2add );
			Logger.printlnLog(LoggerLevel.LL_DEBUG, log2add );
			this.mapUTXOs.remove(transactionOutputId);
		}
	}
	
	// public Set<String> keySet() { return this.mapUTXOs.keySet(); }
	public Set<Entry<String, TransactionOutput> > entrySet() { return this.mapUTXOs.entrySet(); }
	
	public int size() { return this.mapUTXOs.size(); }
	
	/**
	 * Returns balance and stores the UTXO's owned by this wallet in this.UTXOs
	 * @return float : balance
	 */
	public float getBalance(final PublicKey publicKey) {
		float total = 0;
		StringBuilder sb2log = new StringBuilder();
		sb2log.append("(" + this.size() + ")");
		for (Map.Entry<String, TransactionOutput> item : this.entrySet()) {
			TransactionOutput to = item.getValue();
			if (to.isMine(publicKey)) {
				// If output belongs to me ( if coins belong to this key )
				// Add it to our list of unspent transactions.
				total += to.getValue();
				sb2log.append("\t " + to.getValue());
			}
		}
		sb2log.append("\tTOTAL: " + total);
		Logger.printlnLog(LoggerLevel.LL_WARNING, sb2log.toString());
		return total;
	}
	
	/** ***** */
	
	@Override
	protected void setKeyValues() {
		// this.put("mapUTXOs", JSONifiable.generateArray( this.mapUTXOs ) );
	}

	@Override
	protected <T extends JSONifiable> T reloadFrom(String json) 
			throws JSONException { return null; }
	
	@Override
	public String toString() {
		StringBuilder sbToReturn = new StringBuilder();
		sbToReturn.append("mapUTXOs").append(": \n");
		for (Entry<String, TransactionOutput> entry : this.mapUTXOs.entrySet()) 
			{ sbToReturn.append("\t transaction").append(": \n")
				.append( entry.getKey() )
				.append( " : " )
				.append( entry.getValue() )
				.append("\n"); }
		return sbToReturn.toString();
	}
	
	/** ***** */
	
	public static boolean checkBalance(	final Wallet wallet, final float attemptedValue, 
										final TransactionOutputsContainer mapUTXOs) {
		float balanceWallet = wallet.getBalance( mapUTXOs );
		Logger.printlnLog(LoggerLevel.LL_WARNING, "Wallet[" + wallet.getName() + "]'s balance is: " + balanceWallet + " ?= " + attemptedValue);
		return (attemptedValue == balanceWallet);
	}

	public static boolean checkBalances(final Wallet walletA, final float attemptedValueA, 
										final Wallet walletB, final float attemptedValueB, 
										final TransactionOutputsContainer mapUTXOs) {
		boolean checkA = TransactionOutputsContainer.checkBalance(walletA, attemptedValueA, mapUTXOs);
		boolean checkB = TransactionOutputsContainer.checkBalance(walletB, attemptedValueB, mapUTXOs);
		return ( checkA // TransactionOutputsContainer.checkBalance(walletA, attemptedValueA, mapUTXOs)
				 &&
				 checkB // TransactionOutputsContainer.checkBalance(walletB, attemptedValueB, mapUTXOs)
				);
	}
	
}
