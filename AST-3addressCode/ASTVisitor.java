import java.util.*;
public class ASTVisitor implements ASTTestVisitor
{
    //Assignment checks
    private static boolean constCheck = true;
    private static boolean varCheck = true;

    //Arithmetic arguemnts are the same
    private static boolean arithArgsCheck = true;

    private static boolean boolCheck = true;
    private static boolean declaredBeforeUse = true;
    private static boolean duplicates = false;

    private static String scope = "global";
    private static ASTSymbolTable st;
    private static boolean declaredInScope = true;
    private static boolean eachFunctionCalled = true;

    //return total number of errors from run
    private static int numErrors = 0;

    private ArrayList<String> scopes = new ArrayList<>();
    private ArrayList<String> funcs = new ArrayList<>();

    //check for dups

    @Override
    public Object visit(SimpleNode node, Object data) {
        throw new RuntimeException("Visit SimpleNode");
    }

    //public void getType()

    @Override
    public Object visit(ASTProgram node, Object data) {
        st = (ASTSymbolTable) data;
        node.childrenAccept(this, data);
        for(int i = 0; i < scopes.size(); i++){
            String check = st.dupesInScope(scopes.get(i));
            if(check != null){
                duplicates = true;
                numErrors ++;
            }
        }
        System.out.println();
        System.out.println("Total Number of Errors: " + numErrors);
        System.out.println();
        System.out.println("--------------------");
        eachFunctionCalled = eachFuncCalled();
        if(eachFunctionCalled){
            System.out.println("Each Function is called Check: Pass");
        }
        if (declaredInScope) System.out.printf("Scope Check: Passed\n");
        if (declaredBeforeUse) System.out.printf("ID declared before used Check: Pass\n");
        if (constCheck) System.out.printf("Const Type Check: Pass\n");
        if (varCheck) System.out.printf("Variable Type Check: Pass\n");
        if (arithArgsCheck) System.out.printf("Arithmetic Args Check: Pass\n");
        if (boolCheck) System.out.printf("Boolean Check: Pass\n");
        if (!duplicates) System.out.printf("Duplicates in scope Check: Pass\n");

        return DataType.prog;
    }

    @Override
    public Object visit(ASTVar node, Object data) {
        //Make sure not identifier declared twice in same scope but only if its parent is of type var
        // (i.e, its a var_declaration), so it can still be assigned a new value
        SimpleNode parent = (SimpleNode) node.jjtGetParent();
        SimpleNode child1 = (SimpleNode) node.jjtGetChild(0);
        SimpleNode child2 = (SimpleNode) node.jjtGetChild(1);
        String newValue = (String) child1.jjtGetValue();
        node.childrenAccept(this, data);
        return DataType.var_decl;
    }

    @Override
    public Object visit(ASTidentifier node, Object data) {
        SimpleNode parent = (SimpleNode) node.jjtGetParent();
        //Is not a declaration
        if(!parent.toString().equalsIgnoreCase("Var") && !parent.toString().equalsIgnoreCase("Const") &&
                !parent.toString().equalsIgnoreCase("Function")){
            String newValue = (String) node.jjtGetValue();
            //Tiered / prescedence-driven error flagging
            if(!st.inScope(newValue, scope)) {
                if (st.inScope(newValue, "global")) {
                    if (st.typeLookUp(newValue, "global").equalsIgnoreCase("Integer")) {
                        return DataType.Num;
                    }
                    if (st.typeLookUp(newValue, "global").equalsIgnoreCase("boolean")) {
                        return DataType.bool;
                    }
                    return DataType.type_unknown;
                }

                if(declaredBeforeUse == true){
                    declaredInScope = false;
                    System.out.println("(Error in " + scope + ")  Scope Check Failed: ID " + node.jjtGetValue() +
                            " is not in scope: " + scope + "\n");
                    numErrors++;
                    return DataType.type_unknown;
                }
                else{
                    System.out.println("(Error in " + scope + ") " + node.jjtGetValue() +
                            " needs to be declared before use " + scope + "\n");
                    numErrors++;
                }
            }
            else{
                if (st.typeLookUp(newValue, scope).equalsIgnoreCase("Integer")) {
                    return DataType.Num;
                }
                if (st.typeLookUp(newValue, scope).equalsIgnoreCase("boolean")) {
                    return DataType.bool;
                }
                return DataType.type_unknown;
            }
        }
        if(parent.toString() == "Var"){
            return DataType.var_decl;
        }
        if(parent.toString() == "Const"){
            return DataType.const_decl;
        }
        if(parent.toString() == "Function"){
            String sn = (String)node.jjtGetValue();
            funcs.add(sn);
            node.childrenAccept(this, data);
            return DataType.function;
        }
        return DataType.type_unknown;
    }

    @Override
    public Object visit(ASTConst node, Object data) {
        SimpleNode sn = (SimpleNode) node.jjtGetChild(0);
        DataType child1 = (DataType) node.jjtGetChild(1).jjtAccept(this, data);
        DataType child2 = (DataType) node.jjtGetChild(2).jjtAccept(this, data);

        if (child1 != child2) {
            constCheck = false;
            System.out.println("(Error in " + scope + ")  Const Check Failed: CONST " + sn.jjtGetValue() +
                    " assigned a value of the incorrect type" + "\n");
            numErrors++;
            return DataType.type_unknown;
        }
        return DataType.const_decl;
    }

    @Override
    public Object visit(ASTCONSTAssign node, Object data) {
        return (DataType) node.jjtGetChild(0).jjtAccept(this, data);
    }

    @Override
    public Object visit(ASTNum node, Object data) {
        return DataType.Num;
    }

    @Override
    public Object visit(ASTNegate node, Object data) {
        node.childrenAccept(this, data);
        return DataType.Num;
    }

    @Override
    public Object visit(ASTBool node, Object data) {
        node.childrenAccept(this, data);
        return DataType.bool;
    }

    @Override
    public Object visit(ASTFunction node, Object data) {
        SimpleNode id = (SimpleNode) node.jjtGetChild(1);
        scope = (String) id.value;
        scopes.add(scope);
        node.childrenAccept(this, data);
        return DataType.function;
    }

    @Override
    public Object visit(ASTType node, Object data) {
        String s = (String)node.jjtGetValue();
        if(s.equalsIgnoreCase("boolean")){
            return DataType.bool;
        }
        if(s.equalsIgnoreCase("void")){
            return DataType.type_unknown;
        }
        if(s.equalsIgnoreCase("Integer")){
            return DataType.Num;
        }
        return DataType.type_unknown;

    }

    @Override
    public Object visit(ASTParams node, Object data) {
        node.childrenAccept(this, data);
        return DataType.parameter_list;
    }

    @Override
    public Object visit(ASTAdd_Op node, Object data) {
        DataType child1 = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2 = (DataType) node.jjtGetChild(1).jjtAccept(this, data);
        if (child1 != child2) {
            arithArgsCheck = false;
            System.out.println("(Error in " + scope + ")  Arithmetic Argument Check Failed: Cannot add '" + child1 +
                    "' and '" + child2 + "' \n");
            numErrors++;
            return DataType.type_unknown;
        }

        return DataType.Num;
    }

    @Override
    public Object visit(ASTMinus_Op node, Object data) {
        DataType child1 = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2 = (DataType) node.jjtGetChild(1).jjtAccept(this, data);
        if (child1 != child2) {
            arithArgsCheck = false;
            System.out.println("(Error in " + scope + ")  Arithmetic Argument Check Failed: Cannot add '" + child1 +
                    "' and '" + child2 + "' \n");
            numErrors++;
            return DataType.type_unknown;
        }

        return DataType.Num;
    }

    @Override
    public Object visit(ASTArgs node, Object data) {
        node.childrenAccept(this, data);
        return DataType.function;
    }

    @Override
    public Object visit(ASTAssign node, Object data) {
        String id = null;
        SimpleNode child1 = (SimpleNode) node.jjtGetChild(0);
        SimpleNode child2 = (SimpleNode) node.jjtGetChild(1);
        DataType child1DataType = (DataType) child1.jjtAccept(this, data);
        DataType child2DataType = (DataType) child2.jjtAccept(this, data);

        //look up symbol table
        //see if the value of child 1(or 2) exists and if it does find corresponding type
        if (child1DataType != child2DataType) {
            if (child1DataType == DataType.type_unknown) {
                id = st.lookUp((String) child1.value, scope);
                if (id != null) {
                    varCheck = true;
                    return DataType.assign;
                }
                declaredBeforeUse = false;
            }
            if (child2DataType == DataType.type_unknown) {
                id = st.lookUp((String) child2.value, scope);
                if (id != null) {
                    varCheck = true;
                    return DataType.assign;
                }
                declaredBeforeUse = false;
            }
            if(declaredBeforeUse && declaredInScope){
                varCheck = false;
                System.out.println("(Error in " + scope + ")  Variable Type Check Failed: " + child1.value + " " +
                        "was assigned a value of the wrong type. Expecting '" + child1DataType + "' but found '" +
                        child2DataType  + "' \n");
                numErrors++;
                return DataType.type_unknown;
            }
        }
        varCheck = true;
        return DataType.assign;
    }

    @Override
    public Object visit(ASTFuncReturn node, Object data) {
        node.childrenAccept(this, data);
        SimpleNode child1 = (SimpleNode) node.jjtGetChild(0);
        DataType child1DataType = (DataType) child1.jjtAccept(this, data);
        scope = "global";
        return child1DataType;
    }

    @Override
    public Object visit(ASTStm node, Object data) {
        node.childrenAccept(this, data);
        return DataType.statement;
    }

    @Override
    public Object visit(ASTNEMP_PARAMS node, Object data) {
        node.childrenAccept(this, data);
        return DataType.parameter_list;
    }

    @Override
    public Object visit(ASTAND node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTOR node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTLessThanOP node, Object data) {
        DataType child1DataType = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2DataType = (DataType) node.jjtGetChild(1).jjtAccept(this, data);

        if(child1DataType != child2DataType){
            boolCheck = false;
            if(child1DataType == DataType.type_unknown || child2DataType == DataType.type_unknown){
                System.out.println("(Error in " + scope + ") Boolean Check Failed! Encountered Unknown Data Type \n");
                numErrors++;
            }
            else{
                System.out.println("(Error in) " + scope + ") Boolean Check Failed!" + " Cannot compare '" +
                        child1DataType + "' to: '" + child2DataType + "\n");
                numErrors++;
            }
            return DataType.type_unknown;
        }
        return DataType.comp_op;
    }

    @Override
    public Object visit(ASTNotEqOP node, Object data) {
        DataType child1DataType = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2DataType = (DataType) node.jjtGetChild(1).jjtAccept(this, data);

        if(child1DataType != child2DataType){
            boolCheck = false;
            if(child1DataType == DataType.type_unknown || child2DataType == DataType.type_unknown){
                System.out.println("(Error in " + scope + ") Boolean Check Failed! Encountered Unknown Data Type \n");
                numErrors++;
            }
            else{
                System.out.println("(Error in) " + scope + ") Boolean Check Failed!" + " Cannot compare '" +
                        child1DataType + "' to: '" + child2DataType + "' \n");
                numErrors++;
            }
            return DataType.type_unknown;
        }
        return DataType.comp_op;
    }
    @Override
    public Object visit(ASTGreaterThanOP node, Object data) {
        DataType child1DataType = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2DataType = (DataType) node.jjtGetChild(1).jjtAccept(this, data);

        if(child1DataType != child2DataType){
            boolCheck = false;
            if(child1DataType == DataType.type_unknown || child2DataType == DataType.type_unknown){
                System.out.println("(Error in " + scope + ") Boolean Check Failed! Encountered Unknown Data Type \n");
                numErrors++;
            }
            else{
                System.out.println("(Error in) " + scope + ") Boolean Check Failed!" + " Cannot compare '" +
                        child1DataType + "' to: '" + child2DataType + "\n");
                numErrors++;
            }
            return DataType.type_unknown;
        }
        return DataType.comp_op;
    }

    @Override
    public Object visit(ASTEqOP node, Object data) {
        DataType child1DataType = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2DataType = (DataType) node.jjtGetChild(1).jjtAccept(this, data);

        if(child1DataType != child2DataType){
            boolCheck = false;
            if(child1DataType == DataType.type_unknown || child2DataType == DataType.type_unknown){
                System.out.println("(Error in " + scope + ") Boolean Check Failed! Encountered Unknown Data Type \n");
                numErrors++;
            }
            else{
                System.out.println("(Error in) " + scope + ") Boolean Check Failed!" + " Cannot compare '" +
                        child1DataType + "' to: '" + child2DataType + "\n");
                numErrors++;
            }
            return DataType.type_unknown;
        }
        return DataType.comp_op;
    }

    @Override
    public Object visit(ASTGreaterOrEqualOP node, Object data) {
        DataType child1DataType = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2DataType = (DataType) node.jjtGetChild(1).jjtAccept(this, data);

        if(child1DataType != child2DataType){
            boolCheck = false;
            if(child1DataType == DataType.type_unknown || child2DataType == DataType.type_unknown){
                System.out.println("(Error in " + scope + ") Boolean Check Failed! Encountered Unknown Data Type \n");
                numErrors++;
            }
            else{
                System.out.println("(Error in) " + scope + ") Boolean Check Failed!" + " Cannot compare '" +
                        child1DataType + "' to: '" + child2DataType + "\n");
                numErrors++;
            }
            return DataType.type_unknown;
        }
        return DataType.comp_op;
    }

    @Override

    public Object visit(ASTLessOrEqualOP node, Object data) {
        DataType child1DataType = (DataType) node.jjtGetChild(0).jjtAccept(this, data);
        DataType child2DataType = (DataType) node.jjtGetChild(1).jjtAccept(this, data);

        if(child1DataType != child2DataType){
            boolCheck = false;
            if(child1DataType == DataType.type_unknown || child2DataType == DataType.type_unknown){
                System.out.println("(Error in " + scope + ") Boolean Check Failed! Encountered Unknown Data Type \n");
                numErrors++;
            }
            else{
                System.out.println("(Error in) " + scope + ") Boolean Check Failed!" + " Cannot compare '" +
                        child1DataType + "' to: '" + child2DataType +  "\n");
                numErrors++;
            }
            return DataType.type_unknown;
        }
        return DataType.comp_op;
    }

    @Override
    public Object visit(ASTMain node, Object data) {
        scope = "main";
        scopes.add(scope);

        node.childrenAccept(this, data);
        scope = "global";
        return DataType.main;
    }

    @Override
    public Object visit(ASTArgList node, Object data) {
        node.childrenAccept(this, data);
        return DataType.arg_list;
    }

    //Created two lists, one which are function ids and one which are scopes representing functions
    //if the id list is 1 less than the scopes list (which accounts for main), and the elements of the functions list
    //all exist in the scope list, then each function has been called
    public boolean eachFuncCalled(){
        if(funcs.size() == scopes.size()-1){
            for(int i = 0; i<funcs.size(); i++){
                if(scopes.contains(funcs.get(i))){
                    return true;
                }
            }
        }
        return false;
    }

    /*For testing
    public static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }*/
}
