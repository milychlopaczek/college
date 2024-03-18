package cp2023.solution;
import cp2023.base.ComponentId;
import cp2023.base.DeviceId;

import java.util.*;

public class CycleHandler {

    public static List<ComponentId> detectCycle(ComponentId startComponent,
                                                DeviceId startDevice,
                                                Map<ComponentId, DeviceId> componentDestination,
                                                Map<DeviceId, List<IndexedComponent>> deviceIndexedSlots) {
        Set<ComponentId> visited = new HashSet<>();
        Set<ComponentId> recursionStack = new HashSet<>();

        // Call DFS from the starting component
        List<ComponentId> cycle = detectCycleDFS(startComponent, startDevice, componentDestination, deviceIndexedSlots, visited, recursionStack);

        return (cycle != null && !cycle.isEmpty()) ? cycle : null;
    }

    private static List<ComponentId> detectCycleDFS(ComponentId currentComponent,
                                                    DeviceId startDevice,
                                                    Map<ComponentId, DeviceId> componentDestination,
                                                    Map<DeviceId, List<IndexedComponent>> deviceIndexedSlots,
                                                    Set<ComponentId> visited, Set<ComponentId> recursionStack) {
        visited.add(currentComponent);
        recursionStack.add(currentComponent);

        DeviceId destinationDevice = componentDestination.get(currentComponent);

        // Get neighbors (components transferred to the same device and components already placed on the device)
        List<ComponentId> neighbors = getNeighbors(currentComponent, destinationDevice, componentDestination, deviceIndexedSlots);

        for (ComponentId neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                List<ComponentId> result = detectCycleDFS(neighbor, startDevice, componentDestination, deviceIndexedSlots, visited, recursionStack);
                if (result != null) {
                    return result;
                }
            } else if (recursionStack.contains(neighbor) && destinationDevice.equals(startDevice)) {
                // Cycle detected, build the cycle list
                List<ComponentId> cycle = new ArrayList<>();
                cycle.add(neighbor);
                for (ComponentId componentInCycle : recursionStack) {
                    cycle.add(componentInCycle);
                    if (componentInCycle.equals(neighbor)) {
                        break;
                    }
                }
                Collections.reverse(cycle);

                // Print the path for debugging
                System.out.println("Cycle path: " + cycle);

                return cycle;
            }
        }

        recursionStack.remove(currentComponent);
        return null;
    }


    private static List<ComponentId> getNeighbors(ComponentId currentComponent,
                                                  DeviceId destinationDevice,
                                                  Map<ComponentId, DeviceId> componentDestination,
                                                  Map<DeviceId, List<IndexedComponent>> deviceIndexedSlots) {
        List<ComponentId> neighbors = new ArrayList<>();

        // Get components currently on the destination device
        List<IndexedComponent> componentsOnDestinationDevice = deviceIndexedSlots.get(destinationDevice);

        if (componentsOnDestinationDevice != null) {
            for (IndexedComponent indexedComponent : componentsOnDestinationDevice) {
                ComponentId neighbor = indexedComponent.componentId;
                if (!neighbor.equals(currentComponent) && componentDestination.containsKey(neighbor)) {
                    neighbors.add(neighbor);
                }
            }
        }

        return neighbors;
    }


}


