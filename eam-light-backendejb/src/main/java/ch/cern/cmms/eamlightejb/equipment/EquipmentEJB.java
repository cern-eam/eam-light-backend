package ch.cern.cmms.eamlightejb.equipment;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import ch.cern.cmms.eamlightejb.equipment.tools.GraphNode;
import ch.cern.cmms.eamlightejb.index.IndexEJB;
import ch.cern.cmms.eamlightejb.index.IndexGrids;
import ch.cern.cmms.eamlightejb.index.IndexResult;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Entity;
import ch.cern.eam.wshub.core.tools.InforException;

@Stateless
@LocalBean
public class EquipmentEJB {

	@Inject
	private InforClient inforClient;

	@Inject
	private IndexEJB indexEJB;

	@Inject
	private IndexGrids indexGrids;

	/**
	 * Default constructor.
	 */
	public EquipmentEJB() {

	}

	public List<EquipmentChildren> getEquipmentChildren(String equipment) {
		return inforClient.getTools().getEntityManager().createNamedQuery(EquipmentChildren.GET_EQUIPMENT_CHILDREN, EquipmentChildren.class)
				.setParameter("equipment", equipment).getResultList();
	}

	public List<Entity> getEquipmentSearchResults(String code, List<String> customEntityTypes, InforContext inforContext) throws InforException {
		if (customEntityTypes == null) {
			customEntityTypes = Arrays.asList("A", "P", "S", "L");
		}

		List<IndexResult> indexResults;
		if (inforClient.getTools().isDatabaseConnectionConfigured()) {
			indexResults = indexEJB.getIndexResultsFaster(
					code,
					inforContext.getCredentials().getUsername(),
					customEntityTypes
			);

		} else {
			indexResults = indexGrids.search(inforContext, code, customEntityTypes);
		}
		if (indexResults.size() > 10) {
			indexResults = indexResults.subList(0, 9);
		}
		return indexResults.stream().map(r -> new Entity(r.getCode(), r.getDescription(), r.getOrganization())).collect(Collectors.toList());
	}

	public List<GraphNode> getEquipmentStructureTree(String equipment) {
		if (!inforClient.getTools().isDatabaseConnectionConfigured()) {
			return new LinkedList<>();
		}

		// Fetch tree as list
		List<EquipmentTreeNode> result = inforClient.getTools().getEntityManager()
				.createNamedQuery(EquipmentTreeNode.GET_TREE, EquipmentTreeNode.class)
				.setParameter("equipment", equipment)
				.getResultList()
				;

		// Remove root node since its parent is not included
		EquipmentTreeNode rootTreeNode = result.remove(0);
		GraphNode rootNode = new GraphNode(rootTreeNode.getId(), rootTreeNode.getName(), rootTreeNode.getType());

		// Keep cache of nodes so that, when adding a child, it adds throughout the tree (same equipment might have
		// more than one parent
		Map<String, GraphNode> graphNodeMap = new HashMap<>();
		graphNodeMap.put(rootNode.getId(), rootNode);

		// Add the relationships between nodes, create as necessary
		for(EquipmentTreeNode node : result) {
			GraphNode graphNode = graphNodeMap.computeIfAbsent(
					node.getId(),
					k -> new GraphNode(node.getId(), node.getName(), node.getType())
			);

			//Nodes may have parents that are not yet on the tree. Those are ignored.
			graphNodeMap.computeIfPresent(
					node.getParent(),
					(k, v) -> { v.getChildren().add(graphNode); return v; }
			);
		}


		// Fetch root parents if not a location
		if(!"L".equals(rootNode.getType())) {
			List<EquipmentChildren> parents = inforClient.getTools().getEntityManager()
					.createNamedQuery(EquipmentChildren.GET_EQUIPMENT_PARENTS, EquipmentChildren.class)
					.setParameter("equipment", equipment)
					.getResultList()
					;
			rootNode.setParents(parents);
		}

		// Return as List so not to break the API
		return Arrays.asList(rootNode);
	}
}
