package ch.cern.cmms.eamlightejb.equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ch.cern.cmms.eamlightejb.UserTools;
import ch.cern.cmms.eamlightejb.equipment.tools.GraphNode;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.tools.InforException;

/**
 * Session Bean implementation class EquipmentEJB
 */
@Stateless
@LocalBean
public class EquipmentEJB {

	@PersistenceContext
	private EntityManager em;
    @EJB
	private UserTools userTools;

	/**
	 * Default constructor.
	 */
	public EquipmentEJB() {

	}


	public List<EquipmentChildren> getEquipmentChildren(String equipment) {
		return em.createNamedQuery(EquipmentChildren.GET_EQUIPMENT_CHILDREN, EquipmentChildren.class)
				.setParameter("equipment", equipment).getResultList();
	}

	public List<GraphNode> getEquipmentStructureTree(String equipment) {
		// fetch nodes info
		List<EquipmentTreeNode> result = em.createNamedQuery(EquipmentTreeNode.GET_TREE, EquipmentTreeNode.class)
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
				List<EquipmentChildren> parents = em.createNamedQuery(EquipmentChildren.GET_EQUIPMENT_PARENTS, EquipmentChildren.class)
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
