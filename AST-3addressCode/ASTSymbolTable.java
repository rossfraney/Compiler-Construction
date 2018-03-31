import java.util.*;

public class ASTSymbolTable extends Object {
    private Hashtable<String, LinkedList<String>> st;
    private Hashtable<String, String> values;
    private Hashtable<String, String> types;

    ASTSymbolTable() {
        st = new Hashtable<>();
        values = new Hashtable<>();
        types = new Hashtable<>();
        st.put("global", new LinkedList<>());
    }

    public void insert(String id, String value, String type, String scope) {
        LinkedList<String> sc = st.get(scope);
        if (sc == null) { // add new scope
            sc = new LinkedList<>();
            sc.add(id);
            st.put(scope, sc);
        } else {
            sc.addFirst(id);
        }
        //System.out.println(id + scope);
        values.put(id + scope, value);
        types.put(id + scope, type);
    }

    public void print() {
        String scope;
        Enumeration e = st.keys();
        while (e.hasMoreElements()) {
            scope = (String) e.nextElement();
            System.out.println("\nScope: " + scope + "\n------------");
            LinkedList<String> ll = st.get(scope);
            for (String id : ll) {
                String value = values.get(id + scope);
                String type = types.get(id + scope);
                System.out.print(id + ": " + value + "(" + type + ")" + "\n");
            }
        }
    }
        //Need to check for dupes

    public boolean inScope(String id, String scope) {
        LinkedList<String> tmp = st.get(scope);
        /*if (tmp == null)
            return false; // scope doesn't exist*/
        if (tmp.contains(id))
            return true;
        return false;
    }

    //iterate through linked list, if found, remove first occurrence. If found again, return true for dupe detected
    public String dupesInScope(String scope) {
        int i = 0;
        LinkedList<String> tmpList = st.get(scope);
        while(i < tmpList.size() -1){
            Collections.sort(tmpList);
            if (tmpList.size() > 0) {
                String checker = tmpList.pop();
                if(tmpList.contains(checker)){
                    return ("(Error) " + tmpList.get(i) + " is declared twice in scope: " + scope);
                }
            }
        }
        return null;
    }



    public String lookUp(String id, String scope) {
        return values.get(id + scope);
    }
    public String typeLookUp(String id, String scope) {
        return types.get(id + scope);
    }
}

