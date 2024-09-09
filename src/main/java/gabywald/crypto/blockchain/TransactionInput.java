package gabywald.crypto.blockchain;

import gabywald.global.json.JSONException;
import gabywald.global.json.JSONValue;
import gabywald.global.json.JSONifiable;

/**
 * TransactionInput of BlockChain ; to gather informations for making TransactionOuput / Transfert. 
 * @author Gabriel Chandesris (2021, 2024)
 */
public class TransactionInput extends JSONifiable {
	/** Contains the Unspent transaction output */
	private TransactionOutput to = null;
	
	public TransactionInput(TransactionOutput to) 
		{  this.to = to; }
	
	public String getTransactionOutputId() 
		{ return this.to.getId(); }
	
	public TransactionOutput getTransactionOutput() 
		{ return this.to; }
	
	void setTransactionOutput(TransactionOutput to) 
		{ this.to = to; }
	
	@Override
	protected void setKeyValues() {
		this.put("to", JSONValue.instanciate( this.to.toString() ) );
	}

	@Override
	protected <T extends JSONifiable> T reloadFrom(String json) 
			throws JSONException { return null; }
	
	@Override
	public String toString() {
		StringBuilder sbToReturn = new StringBuilder();
		sbToReturn.append("to").append(": ").append( this.to ).append("\n");
		return sbToReturn.toString();
	}
}
