/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package org.opentripplanner.routing.edgetype;

import org.opentripplanner.routing.algorithm.NegativeWeightException;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.StateData.Editor;
import org.opentripplanner.routing.core.TransitStop;
import org.opentripplanner.routing.core.TraverseOptions;
import org.opentripplanner.routing.core.TraverseResult;
import org.opentripplanner.routing.core.Vertex;

/** Applies the local stop rules (see TransitStop.java and LocalStopFinder.java) */
public class PreAlightEdge extends FreeEdge {

    private static final long serialVersionUID = -8046937388471651897L;

    public PreAlightEdge(Vertex from, Vertex to) {
        super(from, to);
    }

    @Override
    public TraverseResult traverse(State s0, TraverseOptions options)
            throws NegativeWeightException {
        State s1 = s0;
        TransitStop toVertex = (TransitStop) getToVertex();
        if (toVertex.isLocal()) {
            Editor editor = s0.edit();
            editor.setAlightedLocal(true);
            s1 = editor.createState();
        }
        return new TraverseResult(0, s1, this);
    }

    @Override
    public TraverseResult traverseBack(State s0, TraverseOptions options)
            throws NegativeWeightException {
        
        if (s0.getData().isAlightedLocal()) {
            // can't alight from a local stop and board another
            return null;
        }
        TransitStop toVertex = (TransitStop) getToVertex();
        if (toVertex.isLocal() && s0.getData().isEverBoarded()) {
            // can't board once one has alighted from a local stop
            return null;
        }
        Editor editor = s0.edit();
        editor.setEverBoarded(true);

        return new TraverseResult(0, editor.createState(), this);
    }

}
