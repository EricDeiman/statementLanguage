import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.antlr.v4.runtime.tree.TerminalNode;

import common.Labeller;

public class FuncMeta {
    public FuncMeta(Labeller labeller, List<TerminalNode> names) {
        name = names.remove(0).getText();
        label = labeller.make(name);
        parameters = new Vector<String>();

        for(TerminalNode param : names) {
            parameters.add(param.getText());
        }
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getParameters() {
        return parameters;
    }

    private String name;
    private String label;
    private List<String> parameters;
}
