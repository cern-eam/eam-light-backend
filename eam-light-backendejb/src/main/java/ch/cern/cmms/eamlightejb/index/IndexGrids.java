package ch.cern.cmms.eamlightejb.index;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

import static ch.cern.eam.wshub.core.tools.DataTypeTools.isNotEmpty;

@ApplicationScoped
public class IndexGrids {

    @Inject
    private InforClient inforClient;

    private List<IndexResult> searchWorkOrders(InforContext inforContext, String keyword, String operator) {
        return searchWorkOrders(inforContext, keyword, operator, null);
    }

    private List<IndexResult> searchWorkOrders(InforContext inforContext, String keyword, String operator, Integer rowCount) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("workordernum", "code");
            map.put("description", "description");
            map.put("department", "mrc");
            map.put("organization", "organization");
            GridRequest gridRequest = new GridRequest("WSJOBS");
            gridRequest.setUserFunctionName("WSJOBS");
            gridRequest.addFilter("workordernum", keyword, operator);
            if (rowCount != null) gridRequest.setRowCount(rowCount);
            List<IndexResult> result = GridTools.convertGridResultToObject(IndexResult.class,
                    map,
                    inforClient.getGridsService().executeQuery(inforContext, gridRequest));
            result.forEach(indexResult -> indexResult.setType("JOB"));
            return result;
        } catch (Exception exception) {
            return null;
        }
    }

    private List<IndexResult> searchEquipment(InforContext inforContext,String keyword, String operator, String gridName, String type, boolean searchExtraColumns) {
        return searchEquipment(inforContext, keyword, operator, gridName, type, searchExtraColumns, null, null);
    }

    private List<IndexResult> searchEquipment(InforContext inforContext, String keyword, String operator, String gridName, String type, boolean searchExtraColumns, String classFilter, Integer rowCount) {
        if (rowCount < 1) {
            return new ArrayList<>();
        }
        try {
            Map<String, String> map = new HashMap<>();
            map.put("equipmentno", "code");
            map.put("equipmentdesc", "description");
            map.put("department", "mrc");
            map.put("alias", "alias");
            map.put("serialnumber", "serial");
            map.put("organization", "organization");
            GridRequest gridRequest = new GridRequest(gridName);
            gridRequest.setUserFunctionName(gridName);

            if (searchExtraColumns) {
                gridRequest.addFilter("alias", keyword, operator, GridRequestFilter.JOINER.OR, true, false);
                gridRequest.addFilter("serialnumber", keyword, operator, GridRequestFilter.JOINER.OR);
            }
            gridRequest.addFilter("equipmentno", keyword, operator, GridRequestFilter.JOINER.AND, false, searchExtraColumns);
            final List<GridRequestFilter> gridRequestFilters = gridRequest.getGridRequestFilters();
            final GridRequestFilter gridRequestFilter = gridRequestFilters.get(gridRequestFilters.size() - 1);
            gridRequestFilter.setForceCaseInsensitive(true);
            gridRequestFilter.setUpperCase(true);

            if (isNotEmpty(classFilter)) {
                gridRequest.addFilter("class", classFilter, "IN", GridRequestFilter.JOINER.AND);
            }

            if (rowCount != null) gridRequest.setRowCount(rowCount);

            //gridRequest.addFilter("udfchar45", keyword, operator, GridRequestFilter.JOINER.OR);
            List<IndexResult> result = GridTools.convertGridResultToObject(IndexResult.class,
                    map,
                    inforClient.getGridsService().executeQuery(inforContext, gridRequest));
            result.forEach(indexResult -> indexResult.setType(type));
            return result;
        } catch (Exception exception) {
            return null;
        }
    }

    private List<IndexResult> searchParts(InforContext inforContext, String keyword, String operator) {
        return searchParts(inforContext, keyword, operator, null, null);
    }

    public List<IndexResult> searchParts(InforContext inforContext, String keyword, String operator, String classFilter, Integer rowCount) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("partcode", "code");
            map.put("description", "description");
            map.put("organization", "organization");
            GridRequest gridRequest = new GridRequest("SSPART");
            gridRequest.setUserFunctionName("SSPART");
            gridRequest.addFilter("partcode", keyword, operator, GridRequestFilter.JOINER.AND);
            if (isNotEmpty(classFilter)) {
                gridRequest.addFilter("class", classFilter, "IN", GridRequestFilter.JOINER.AND);
            }
            if (rowCount != null) gridRequest.setRowCount(rowCount);
            List<IndexResult> result = GridTools.convertGridResultToObject(IndexResult.class,
                    map,
                    inforClient.getGridsService().executeQuery(inforContext, gridRequest));
            result.forEach(indexResult -> indexResult.setType("PART"));
            return result;
        } catch (Exception exception) {
            return null;
        }
    }

    public List<IndexResult> search(InforContext inforContext, String keyword, List<String> entityTypes) throws InforException {
        return search(inforContext, keyword, entityTypes, null, null);
    }

    public List<IndexResult> search(InforContext inforContext, String keyword, List<String> entityTypes,
                                    String entityClass, Integer rowCountTemp) throws InforException {
        List<IndexResult> result = new LinkedList<>();
        List<Runnable> runnables = new LinkedList<>();
        final Integer rowCount = rowCountTemp == null ? 10 : rowCountTemp;

        Map<String, Runnable> entityTypeRunnableMap = new HashMap<>();
        entityTypeRunnableMap.put("JOB", () -> result.addAll(searchWorkOrders(inforContext, keyword, "BEGINS", rowCount - result.size())));
        entityTypeRunnableMap.put("L", () -> result.addAll(searchEquipment(inforContext, keyword, "BEGINS", "OSOBJL", "L", false, entityClass, rowCount - result.size())));
        entityTypeRunnableMap.put("A", () -> result.addAll(searchEquipment(inforContext, keyword, "BEGINS", "OSOBJA", "A", true, entityClass, rowCount - result.size())));
        entityTypeRunnableMap.put("P", () -> result.addAll(searchEquipment(inforContext, keyword, "BEGINS", "OSOBJP", "P", true, entityClass, rowCount - result.size())));
        entityTypeRunnableMap.put("S", () -> result.addAll(searchEquipment(inforContext, keyword, "BEGINS", "OSOBJS", "S", true, entityClass, rowCount - result.size())));
        entityTypeRunnableMap.put("PART", () -> result.addAll(searchParts(inforContext, keyword, "BEGINS", entityClass, rowCount - result.size())));

        entityTypeRunnableMap.entrySet().stream()
                .filter(entry -> entityTypes.contains(entry.getKey()))
                .forEach(entry -> runnables.add(entry.getValue()));

        inforClient.getTools().processRunnables(runnables);
        return result;
    }

    public IndexResult searchSingleResult(InforContext inforContext, String keyword) throws InforException {
        List<IndexResult> result = new LinkedList<>();
        List<Runnable> runnables = new LinkedList<>();

        runnables.add(() -> result.addAll(searchWorkOrders(inforContext, keyword,"EQUALS")));
        runnables.add(() -> result.addAll(searchEquipment(inforContext, keyword,"EQUALS","OSOBJA","A", true)));
        runnables.add(() -> result.addAll(searchEquipment(inforContext, keyword,"EQUALS","OSOBJP","P", true)));
        runnables.add(() -> result.addAll(searchEquipment(inforContext, keyword,"EQUALS","OSOBJS","S", true)));
        runnables.add(() -> result.addAll(searchEquipment(inforContext, keyword,"EQUALS","OSOBJL","L", false)));
        runnables.add(() -> result.addAll(searchParts(inforContext, keyword, "EQUALS")));

        inforClient.getTools().processRunnables(runnables);

        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

}
