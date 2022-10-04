package ch.cern.cmms.eamlightweb.equipment.structure;

import ch.cern.cmms.eamlightejb.equipment.EquipmentChildren;
import ch.cern.cmms.eamlightejb.equipment.EquipmentEJB;
import ch.cern.cmms.eamlightejb.equipment.tools.GraphNode;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

@ApplicationScoped
public class EquipmentStructure
{
    @EJB
    private EquipmentEJB equipmentEJB;
    @Inject
    private AuthenticationTools authenticationTools;
    @Inject
    private InforClient inforClient;

    private int counter = 0;

    public List<GraphNode> readEquipmentTree(String eqID, String org, String type) throws InforException {
        if (inforClient.getTools().isDatabaseConnectionConfigured()) {
            return equipmentEJB.getEquipmentStructureTree(eqID);
        }

        GraphNode current = new GraphNode();
        current.setId(eqID);
        current.setType(type);
        current.setIdOrg(org);
        current.setParents(inforClient.getTools().getGridTools().convertGridResultToObject(EquipmentChildren.class,
                null,
                inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), getGridRequest("stc_child", current.getId()))));
        attachChildren(inforClient, authenticationTools.getInforContext(), current);

        return new LinkedList<>(Arrays.asList(current));
    }


    private GraphNode attachChildren(InforClient inforClient, InforContext context, GraphNode root) {

        List<GraphNode> nodes = new LinkedList<>();
        nodes.add(root);

        while (!nodes.isEmpty()) {

            LinkedList<GraphNode> nodesCopy = new LinkedList<>(nodes);
            LinkedList<Runnable> runnables = new LinkedList<>();
            nodes.clear();

            nodesCopy.forEach(node -> {
                runnables.add( () -> {
                    List<GraphNode> children = getChildren(inforClient, context, node.getId());
                    if (children != null) {
                        node.setChildren(children);
                        nodes.addAll(children);
                    }
                });
            });

            try {
                inforClient.getTools().processRunnables(runnables);
            } catch (Exception e) {
                inforClient.getTools().log(Level.SEVERE, "Error " + e.getMessage());
                e.printStackTrace();
            }
        }
        return root;
    }

    private List<GraphNode> getChildren(InforClient inforClient, InforContext context, String code) {
        try {
            return inforClient.getTools().getGridTools().convertGridResultToObject(GraphNode.class,
                    null,
                    inforClient.getGridsService().executeQuery(context, getGridRequest("stc_parent", code)));

        } catch (InforException inforException) {
            inforClient.getTools().log(Level.SEVERE, "Error " + inforException.getMessage());
            return null;
        }
    }

    private GridRequest getGridRequest(String codeKey, String codeValue) {
        GridRequest gridRequest = new GridRequest("OCSTRU");
        gridRequest.addParam("parameter.lastupdated", "1-JAN-1970");
        gridRequest.addFilter(codeKey, codeValue, "=");
        return gridRequest;
    }

}
