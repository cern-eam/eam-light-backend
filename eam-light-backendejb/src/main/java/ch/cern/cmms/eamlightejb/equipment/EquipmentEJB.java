package ch.cern.cmms.eamlightejb.equipment;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import ch.cern.cmms.eamlightejb.equipment.tools.GraphNode;
import ch.cern.eam.wshub.core.client.InforClient;


@Stateless
@LocalBean
public class EquipmentEJB {

	@Inject
	private InforClient inforClient;

	/**
	 * Default constructor.
	 */
	public EquipmentEJB() {

	}

	public List<EquipmentChildren> getEquipmentChildren(String equipment) {
		return inforClient.getTools().getEntityManager().createNamedQuery(EquipmentChildren.GET_EQUIPMENT_CHILDREN, EquipmentChildren.class)
				.setParameter("equipment", equipment).getResultList();
	}

	public List<GraphNode> getEquipmentStructureTree(String equipment) {
		if (!inforClient.getTools().isDatabaseConnectionConfigured()) {
			return new LinkedList<GraphNode>();
		}

		// fetch nodes info
		List<EquipmentTreeNode> result = inforClient.getTools().getEntityManager().createNamedQuery(EquipmentTreeNode.GET_TREE, EquipmentTreeNode.class)
				.setParameter("equipment", equipment) 
				.getResultList();
		
		// root node
		EquipmentTreeNode rootTreeNode = result.get(0);		
		
		// collect acyclic directed graph data
		Map<String, List<GraphNode>> graphNodesMap = new HashMap<String, List<GraphNode>>();	
		for(EquipmentTreeNode node : result) { 
			
			List<GraphNode> graphNodes = graphNodesMap.get(node.getId()); 		 			
			if (graphNodes == null) {
				graphNodes = new ArrayList();
				graphNodesMap.put(node.getId(), graphNodes);
			}	
			
			if (graphNodes.isEmpty()) {
				GraphNode graphNode = new GraphNode(node.getId(), node.getName(), node.getType());
				graphNodes.add(graphNode);
			}

			if (node.getParent() != null) { 
				int numberNodes = graphNodes.size();
				for (int i=0; i<numberNodes; i++){
					GraphNode graphNode = graphNodes.get(i);
					List<GraphNode> parentNodes = graphNodesMap.get(node.getParent());
					
					for(GraphNode parentNode : parentNodes) {
						if (graphNode.getParent() == null) {
							graphNode.setParent(parentNode);
							parentNode.getChildren().add(graphNode);
						} else {
							if (graphNode.getParent() != parentNode) {
								cloneNode(graphNodesMap, graphNode, parentNode);
							}
						}
					}
				}
			}			
		}
		
		// set up the root node
		List<GraphNode> rootNodes = graphNodesMap.get(rootTreeNode.getId());
		
		// reset info on node parent
		for (List<GraphNode> nodes : graphNodesMap.values()) {
			for (GraphNode node : nodes) {
				if (!node.getId().equals(rootNodes.get(0).getId())) {
					node.setParent(null);
				}
			}
		}
		
		// fetch list of multiple parents if it exists
		if(rootNodes != null && rootNodes.size()>0) {
			// if it is not a Location, then we fetch its parents
			if(!rootNodes.get(0).getType().equals("L")) {
				List<EquipmentChildren> parents = inforClient.getTools().getEntityManager().createNamedQuery(EquipmentChildren.GET_EQUIPMENT_PARENTS, EquipmentChildren.class)
						.setParameter("equipment", equipment).getResultList();
			
				rootNodes.get(0).setParents(parents);
			}
		}
		
		return rootNodes;
	}
	
	/*
	 * Used by getEquipmentStructureTree to copy an existing node
	 */
	private void cloneNode(Map<String, List<GraphNode>> graphNodesMap, GraphNode graphNode, GraphNode parentNode) {
		GraphNode clonedGraphNode =  new GraphNode(graphNode.getId(), graphNode.getName(), graphNode.getType());
		graphNodesMap.get(graphNode.getId()).add(clonedGraphNode);
		clonedGraphNode.setParent(parentNode);
		parentNode.getChildren().add(clonedGraphNode);
		for(GraphNode child : graphNode.getChildren()) {
			cloneNode(graphNodesMap, child, clonedGraphNode);
		}
	}

}
