/**
 * AutoMerge
 * Copyright (C) 2013-2014 Olaf Lessenich
 * Copyright (C) 2014-2017 University of Passau, Germany
 * Copyright (C) 2018-2019 Fengmin Zhu
 * Copyright (C) 2019-2020 Tsinghua University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 * Olaf Lessenich <lessenic@fim.uni-passau.de>
 * Georg Seibt <seibt@fim.uni-passau.de>
 * Fengmin Zhu <zfm17@mails.tsinghua.edu.cn>
 */
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
