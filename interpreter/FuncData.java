import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.antlr.v4.runtime.tree.TerminalNode;

import parser.StmntParser.BlockContext;

public class FuncData {

    public FuncData(List<TerminalNode> names, BlockContext body) {
        this.body = body;
        this.name = names.remove(0).getText();
        parameters = new Vector<String>();

        for(TerminalNode id : names) {
            parameters.add(id.getText());
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public BlockContext getBody() {
        return body;
    }

    private String name;
    private List<String> parameters;
    private BlockContext body;
}
