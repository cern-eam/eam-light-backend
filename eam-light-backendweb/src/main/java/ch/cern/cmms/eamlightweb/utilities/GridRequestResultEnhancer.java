package ch.cern.cmms.eamlightweb.utilities;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;

public class GridRequestResultEnhancer extends EAMLightController {

    /**
     * Generic function to perform operations on a GridRequestResult.
     */
    public interface GridRequestExecutor {
        void execute(GridRequestResult result);
    }

    /**
     * Utility function to execute a grid request with a given action.
     */
    public static void performGridRequestAction(
            GridRequest gridRequest,
            InforClient inforClient,
            AuthenticationTools authenticationTools,
            GridRequestExecutor action) {
        try {
            GridRequestResult result = inforClient.getGridsService().executeQuery(
                    authenticationTools.getR5InforContext(), gridRequest);
            action.execute(result);
        } catch (InforException e) {
            throw new RuntimeException("Error executing GridRequest", e);
        }
    }

    /**
     * Function to map values from a key-value store to a list of objects.
     */

    public static <T, K, V> Consumer<List<T>> performMatch(
            Function<T, K> getKeyFn,
            BiConsumer<T, V> setValueFn,
            Map<K, V> lookupMap,
            V val) {

        return list -> list.forEach(item -> {

           if(getKeyFn!=null) {
               K key = getKeyFn.apply(item);
               V value = (lookupMap != null) ? lookupMap.get(key) : null;

               if (value != null) {
                   setValueFn.accept(item, value);
               }
           }else{
                setValueFn.accept(item, val);
            }
        });
    }


    /**
     * Utility method to filter missing fields.
     */
    public static <T> BiFunction<List<? extends T>, Function<T, String>, String> filterMissingFields() {
        return (list, filterFn) -> list.stream()
                .map(filterFn)
                .filter(item -> !checkEmpty(item))
                .collect(Collectors.joining(","));
    }

    /**
     * Creates a GridRequest with filtered values.
     */
    public static <T> Function<List<T>, Function<Function<T, String>, Function<String, GridRequest>>>
    missingFieldGridRequest(String gridName, GridRequest.GRIDTYPE gridType) {
        return result -> getFilter -> codeKey -> {
            GridRequest gridRequest = new GridRequest(gridName, gridType);
            String value = filterMissingFields().apply(result, (Function<Object, String>) getFilter);
            if (!value.isEmpty()) {
                gridRequest.addFilter(codeKey, value, "IN", GridRequestFilter.JOINER.OR);
            }
            return gridRequest;
        };
    }


    /**
     * Utility method to check if a string is empty or null.
     */
    public static boolean checkEmpty(String element) {
        return element == null || element.trim().isEmpty();
    }
}
