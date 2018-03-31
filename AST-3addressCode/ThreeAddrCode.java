public class ThreeAddrCode implements ASTTestVisitor {

    private static int numLabels = 1;
    private static int tCounter = 1;

    @Override
    public Object visit(SimpleNode node, Object data) {
        throw new RuntimeException("Visit SimpleNode");
    }

    @Override
    public Object visit(ASTProgram node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTVar node, Object data) {
        String id = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String val = (String) node.jjtGetChild(1).jjtAccept(this, data);

        System.out.println("        "+ id + " = " + val);

        return null;
    }

    @Override
    public Object visit(ASTidentifier node, Object data) {
        return node.value;
    }

    @Override
    public Object visit(ASTConst node, Object data) {
        String id = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String val = (String) node.jjtGetChild(2).jjtAccept(this, data);
        System.out.println("        " + id + " = " + val );
        return null;
    }

    @Override
    public Object visit(ASTCONSTAssign node, Object data) {
        return node.value + " " + ((String) node.jjtGetChild(0).jjtAccept(this, data));
    }

    @Override
    public Object visit(ASTNum node, Object data) {
        return node.value;
    }

    @Override
    public Object visit(ASTNegate node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTBool node, Object data) {
        return node.value;
    }

    @Override
    public Object visit(ASTFunction node, Object data) {
        SimpleNode id = (SimpleNode) node.jjtGetChild(1);
        System.out.println((String) id.value + ":     ");
        numLabels++;
        node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTType node, Object data) {
        return node.value;
    }

    @Override
    public Object visit(ASTParams node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTAdd_Op node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String t = "t" + tCounter;

        System.out.println("        " + t + " = " + child1 + " " + node.value + " " + child2 );
        return t;
    }

    @Override
    public Object visit(ASTMinus_Op node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String t = "t" + tCounter;

        System.out.println("        " + t + " = " + child1 + " " + node.value + " " + child2 );
        return t;

    }

    @Override
    public Object visit(ASTAssign node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);
        if(child2 == null){
            SimpleNode id = (SimpleNode) node.jjtGetChild(1);
            child2 = ("-" + (String)id.value);
        }
        if(child2.equals("multiply")){
            SimpleNode id = (SimpleNode) node.jjtGetChild(0);
            SimpleNode id2 = (SimpleNode) node.jjtGetChild(1);
        }
        String s = child1 + " " + node.value + " " + child2;
        System.out.println("        " + s );
        return (Object) s;
    }

    @Override
    public Object visit(ASTFuncReturn node, Object data) {
        node.childrenAccept(this, data);
        System.out.println("        return");
        return node.value;
    }

    @Override
    public Object visit(ASTStm node, Object data) {
        String stm = (String) node.value;
        String condition;
        if(((String)node.value).equals("while")){
            System.out.println("L" + numLabels + ":     ");
            numLabels++;
            condition = (String) node.jjtGetChild(0).jjtAccept(this, data);
            System.out.println("        IfFalse " + condition + " GOTO L" + numLabels + ":     ");
            for(int i = 1; i< node.jjtGetNumChildren(); i++){
                node.jjtGetChild(i).jjtAccept(this, data);
            }
            return null;
        }
        if(((String) node.value).equals("if")){
            System.out.println("L" + numLabels + ":     ");
            condition = (String) node.jjtGetChild(0).jjtAccept(this, data);
            System.out.println("        IfFalse " + condition + " GOTO L" + numLabels + ":     ");
            numLabels++;
            node.jjtGetChild(1).jjtAccept(this, data);
            System.out.println("L" + numLabels + ":     ");
            numLabels++;
            return null;
        }
        return null;
    }

    @Override
    public Object visit(ASTNEMP_PARAMS node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    @Override//ToDo Get to work
    public Object visit(ASTArgs node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);
        String s = ("( " + child1 + " ), ( " + child2 + " )");
        return s;
    }

    @Override
    public Object visit(ASTAND node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String t = "t" + tCounter++;
        System.out.println("        " + t + " = " + child1 + " " + node.value + " " + child2 );
        return t;
    }

    @Override
    public Object visit(ASTOR node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String t = "t" + tCounter++;
        System.out.println("        " + t + " = " + child1 + " " + node.value + " " + child2 );
        return t;
    }

    @Override
    public Object visit(ASTLessThanOP node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String t = "t" + tCounter++;
        System.out.println("        " + t + " = " + child1 + " " + node.value + " " + child2 );
        return t;
    }

    @Override
    public Object visit(ASTNotEqOP node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String t = "t" + tCounter++;
        System.out.println("        " + t + " = " + child1 + " " + node.value + " " + child2 );
        return t;

    }
    @Override
    public Object visit(ASTGreaterThanOP node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String t = "t" + tCounter++;
        System.out.println("        " + t + " = " + child1 + " " + node.value + " " + child2 );
        return t;
    }

    @Override
    public Object visit(ASTEqOP node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String t = "t" + tCounter++;
        System.out.println("        " + t + " = " + child1 + " " + node.value + " " + child2 );
        return t;
    }

    @Override
    public Object visit(ASTGreaterOrEqualOP node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String t = "t" + tCounter++;
        System.out.println("        " + t + " = " + child1 + " " + node.value + " " + child2 );
        return t;
    }

    @Override

    public Object visit(ASTLessOrEqualOP node, Object data) {
        String child1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String child2 = (String) node.jjtGetChild(1).jjtAccept(this, data);

        String t = "t" + tCounter++;
        System.out.println("        " + t + " = " + child1 + " " + node.value + " " + child2 );
        return t;
    }

    @Override
    public Object visit(ASTMain node, Object data) {
        System.out.println("main:     ");
        numLabels++;
        node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTArgList node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            System.out.println("        param" + ((String) node.jjtGetChild(i).jjtAccept(this, data) ));
        }
        return (Object) 1;
    }
}
