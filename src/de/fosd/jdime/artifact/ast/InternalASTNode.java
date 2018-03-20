package de.fosd.jdime.artifact.ast;

import de.fosd.jdime.config.merge.Revision;

import java.io.*;

/**
 * Warning: This class is for experiment ONLY.
 */
public class InternalASTNode extends ASTNodeArtifact {
    public InternalASTNode(File file) {
        super(new Revision("internal"));
        FileReader reader = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bReader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String s = "";
        try {
            while ((s = bReader.readLine()) != null) {
                sb.append(s + "\n");
            }
            bReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        code = sb.toString().trim();
    }

    private String code = "";

    @Override
    public boolean eq(ASTNodeArtifact that) {
        return code.equals(that.astnode.prettyPrint());
    }

    @Override
    public String prettyPrint() {
        return code;
    }
}
