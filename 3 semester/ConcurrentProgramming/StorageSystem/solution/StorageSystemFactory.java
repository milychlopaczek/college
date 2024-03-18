/*
 * University of Warsaw
 * Concurrent Programming Course 2023/2024
 * Java Assignment
 *
 * Author: Konrad Iwanicki (iwanicki@mimuw.edu.pl)
 */
package cp2023.solution;

import java.util.Map;

import cp2023.base.ComponentId;
import cp2023.base.DeviceId;
import cp2023.base.StorageSystem;


public final class StorageSystemFactory {

    public static StorageSystem newSystem(
            Map<DeviceId, Integer> deviceTotalSlots,
            Map<ComponentId, DeviceId> componentPlacement) {
        if (deviceTotalSlots == null || componentPlacement == null) {
            throw new IllegalArgumentException();
        }
        for (Map.Entry<ComponentId, DeviceId> entry : componentPlacement.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                throw new IllegalArgumentException();
            }
        }
        return new StorageSystemImpl(deviceTotalSlots, componentPlacement);
    }

}
