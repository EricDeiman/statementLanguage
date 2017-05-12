import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.antlr.v4.runtime.tree.TerminalNode;

import parser.StmntParser.FuncBodyContext;

public class FuncData {

    public FuncData(List<TerminalNode> names, FuncBodyContext body) {
        this.body = body;
        this.name = names.remove(0).getText();
        parameters = new Vector<String>();

        for(TerminalNode id : names) {
            parameters.add(id.getText());
        }

        internalName = String.format("%s\\%d", name, parameters.size());
    }

    public String getName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public FuncBodyContext getBody() {
        return body;
    }

    private String name;
    private List<String> parameters;
    private FuncBodyContext body;
    private String internalName;
}
