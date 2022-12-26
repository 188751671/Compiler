import java.util.HashMap;

public class SymbolTable {
    private HashMap<String,Integer> table; //  1== integer  2==character
    public SymbolTable() {
        table = new HashMap<>();
    }
    public boolean isInTable(String symbol){
        return table.containsKey(symbol);
    }
    public int getType(String symbol){
        return table.get(symbol);
    }
    public boolean set(String symbol,Integer type){
        table.put(symbol,type);
        return true;
    }
}
