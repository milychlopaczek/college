package cp2023.solution;

import cp2023.base.ComponentId;
import cp2023.base.ComponentTransfer;
import cp2023.base.DeviceId;
import cp2023.base.StorageSystem;
import cp2023.exceptions.ComponentAlreadyExists;
import cp2023.exceptions.ComponentDoesNotExist;
import cp2023.exceptions.ComponentDoesNotNeedTransfer;
import cp2023.exceptions.ComponentIsBeingOperatedOn;
import cp2023.exceptions.DeviceDoesNotExist;
import cp2023.exceptions.IllegalTransferType;
import cp2023.exceptions.TransferException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class StorageSystemImpl implements StorageSystem {
    public static final String PANIC_UNEXPECTED_THREAD_INTERRUPTION = "panic: unexpected thread interruption";

    private final Semaphore mutex = new Semaphore(1, true);
    private final Map<DeviceId, Integer> deviceFreeSlots = new ConcurrentHashMap<>();
    private final Map<ComponentId, Semaphore> componentSemaphore = new ConcurrentHashMap<>();
    private final Map<ComponentId, Semaphore> componentWaitingForSlot = new ConcurrentHashMap<>();
    private final Map<ComponentId, DeviceId> componentDestination = new LinkedHashMap<>();
    private final Map<DeviceId, List<IndexedComponent>> deviceIndexedSlots = new HashMap<>();
    private final Map<ComponentId, Integer> componentIndex = new HashMap<>();
    private final Map<DeviceId, Integer> deviceTotalSlots;
    private final Map<ComponentId, DeviceId> componentPlacement;
    private final Map<DeviceId, Integer> deviceCurrentCapacity = new ConcurrentHashMap<>();

    private void InitiateDevices() {
        for (Map.Entry<DeviceId, Integer> device : deviceTotalSlots.entrySet()) {
            List<IndexedComponent> components = new ArrayList<>();
            for (int i = 0; i < device.getValue(); i++) {
                components.add(new IndexedComponent(null, i));
            }
            deviceIndexedSlots.put(device.getKey(), components);
        }
        for (Map.Entry<ComponentId, DeviceId> component : componentPlacement.entrySet()) {
            List<IndexedComponent> components = deviceIndexedSlots.get(component.getValue());
            for (IndexedComponent indexedComponent : components) {
                if (indexedComponent.componentId == null) {
                    indexedComponent.componentId = component.getKey();
                    indexedComponent.semaphore = new Semaphore(0);
                    componentIndex.put(component.getKey(), indexedComponent.index);
                    break;
                }
            }
        }
    }

    private void removeComponentFromIndexedDevice(ComponentId componentId, DeviceId deviceId) {
        List<IndexedComponent> components = deviceIndexedSlots.get(deviceId);
        for (IndexedComponent indexedComponent : components) {
            if (indexedComponent.componentId != null && indexedComponent.componentId.equals(componentId)) {
                indexedComponent.componentId = null;
                break;
            }
        }
    }

    private int reserveFreeSpotOnIndexedDevice(ComponentId componentId, DeviceId deviceId) {
        List<IndexedComponent> components = deviceIndexedSlots.get(deviceId);
        for (IndexedComponent indexedComponent : components) {
            if (indexedComponent.componentId == null) {
                indexedComponent.componentId = componentId;
                componentIndex.put(componentId, indexedComponent.index);
                return indexedComponent.index;
            }
        }
        return -1;
    }

    private void reserveSpotOnIndexedDevice(ComponentId componentId, DeviceId deviceId, int index) {
        List<IndexedComponent> components = deviceIndexedSlots.get(deviceId);
        IndexedComponent indexedComponent = components.get(index);
        if (indexedComponent != null) {
            indexedComponent.componentId = componentId;
        }
    }

    public StorageSystemImpl(Map<DeviceId, Integer> deviceTotalSlots, Map<ComponentId, DeviceId> componentPlacement) {
        this.deviceTotalSlots = new ConcurrentHashMap<>(deviceTotalSlots);
        this.componentPlacement = new ConcurrentHashMap<>(componentPlacement);
        InitiateDevices();
        for (DeviceId deviceId : deviceTotalSlots.keySet()) {
            deviceCurrentCapacity.put(deviceId, 0);
        }
        for (Map.Entry<ComponentId, DeviceId> placement : componentPlacement.entrySet()) {
            deviceCurrentCapacity.compute(placement.getValue(), (key, val) -> val + 1);
            componentSemaphore.put(placement.getKey(), new Semaphore(1, true));
            componentWaitingForSlot.put(placement.getKey(), new Semaphore(0, true));
        }
        for (Map.Entry<DeviceId, Integer> device : deviceTotalSlots.entrySet()) {
            int availableSlots = device.getValue() - deviceCurrentCapacity.get(device.getKey());
            if (availableSlots < 0) {
                throw new IllegalArgumentException();
            }
            deviceFreeSlots.put(device.getKey(), availableSlots);
        }
    }

    private void handleWrongTransfers(final ComponentTransfer transfer) throws TransferException {
        final ComponentId componentId = transfer.getComponentId();
        final DeviceId sourceDeviceId = transfer.getSourceDeviceId();
        final DeviceId destinationDeviceId = transfer.getDestinationDeviceId();

        if (sourceDeviceId != null) {
            Semaphore semaphore = componentSemaphore.get(componentId);
            if (semaphore != null) {
                if (!semaphore.tryAcquire()) {
                    throw new ComponentIsBeingOperatedOn(componentId);
                } else {
                    semaphore.release();
                }
            }
        }

        if (sourceDeviceId == null && destinationDeviceId == null) {
            throw new IllegalTransferType(componentId);
        } else if (sourceDeviceId != null && !deviceTotalSlots.containsKey(sourceDeviceId)) {
            throw new DeviceDoesNotExist(sourceDeviceId);
        } else if (destinationDeviceId != null && !deviceTotalSlots.containsKey(destinationDeviceId)) {
            throw new DeviceDoesNotExist(destinationDeviceId);
        } else if (sourceDeviceId == null && componentPlacement.containsKey(componentId)) {
            DeviceId device = componentPlacement.get(componentId);
            if (device != null) {
                throw new ComponentAlreadyExists(componentId, device);
            }
            throw new ComponentAlreadyExists(componentId);
        } else if (sourceDeviceId != null && (!componentPlacement.containsKey(componentId) || !componentPlacement.get(componentId).equals(sourceDeviceId))) {
            throw new ComponentDoesNotExist(componentId, sourceDeviceId);
        } else if (destinationDeviceId != null && destinationDeviceId.equals(componentPlacement.get(componentId))) {
            throw new ComponentDoesNotNeedTransfer(componentId, destinationDeviceId);
        }
    }
    private ComponentId findReplacementComponent(DeviceId deviceId) {
        for (Map.Entry<ComponentId, DeviceId> placement : componentDestination.entrySet()) {
            if (placement.getValue().equals(deviceId)) {
                componentDestination.remove(placement.getKey());
                return placement.getKey();
            }
        }
        return null;
    }

    private void addComponentToDevice(ComponentId componentId, DeviceId deviceId) {
        componentPlacement.put(componentId, deviceId);
        deviceCurrentCapacity.compute(deviceId, (key, val) -> val + 1);
        deviceFreeSlots.compute(deviceId, (key, val) -> val - 1);
    }
    private void removeComponentFromDevice(ComponentId componentId, DeviceId deviceId) {
        componentPlacement.remove(componentId, deviceId);
        deviceCurrentCapacity.compute(deviceId, (key, val) -> val - 1);
        deviceFreeSlots.compute(deviceId, (key, val) -> val + 1);
    }

    private void transferNewComponentToDevice(ComponentId componentId, DeviceId deviceId, ComponentTransfer transfer) throws InterruptedException {
        // Introducing new component to the system.
        componentWaitingForSlot.put(componentId, new Semaphore(0, true));
        int myIndex = -1;
        if (deviceFreeSlots.get(deviceId) == 0) {
            // We have to wait for someone to free up a slot.
            componentDestination.put(componentId, deviceId);
            mutex.release();
            componentWaitingForSlot.get(componentId).acquire();
            // Someone has awakened us so we can continue.
            mutex.acquire();
            myIndex = componentIndex.get(componentId);
            componentPlacement.put(componentId, deviceId);
        } else { // There is a slot :)
            addComponentToDevice(componentId, deviceId);
            myIndex = reserveFreeSpotOnIndexedDevice(componentId, deviceId);
        }
        mutex.release();
        transfer.prepare();
        deviceIndexedSlots.get(deviceId).get(myIndex).semaphore.acquire();
        transfer.perform();
    }

    private void deleteComponentFromDevice(ComponentId componentId, DeviceId deviceId, ComponentTransfer transfer) {
        Semaphore prepareSemaphore = deviceIndexedSlots.get(deviceId).get(componentIndex.get(componentId)).semaphore;
        int myIndex = componentIndex.get(componentId);
        ComponentId componentToWake = findReplacementComponent(deviceId);
        if (componentToWake != null) {
            componentIndex.put(componentToWake, myIndex);
            reserveSpotOnIndexedDevice(componentToWake, deviceId, myIndex);
            componentWaitingForSlot.get(componentToWake).release();
        } else {
            removeComponentFromIndexedDevice(componentId, deviceId);
            removeComponentFromDevice(componentId, deviceId);
        }
        componentPlacement.remove(componentId);
        mutex.release();
        transfer.prepare();
        prepareSemaphore.release();
        transfer.perform();
    }

    private void TransferComponentBetweenDevices(ComponentId componentId, DeviceId sourceDeviceId,
                                                 DeviceId destinationDeviceId, ComponentTransfer transfer) throws InterruptedException {
        Semaphore previousPrepareSemaphore = deviceIndexedSlots.get(sourceDeviceId).get(componentIndex.get(componentId)).semaphore;
        int myIndex = componentIndex.get(componentId);
        if (deviceFreeSlots.get(destinationDeviceId) == 0) {
            // We have to wait for someone to free up a slot.
            componentDestination.put(componentId, destinationDeviceId);
            mutex.release();
            componentWaitingForSlot.get(componentId).acquire();
            mutex.acquire();
            componentPlacement.put(componentId, destinationDeviceId);
            int myNewIndex = componentIndex.get(componentId);
            ComponentId componentToWake = findReplacementComponent(sourceDeviceId);
            if (componentToWake != null) {
                componentIndex.put(componentToWake, myIndex);
                reserveSpotOnIndexedDevice(componentToWake, sourceDeviceId, myIndex);
                componentWaitingForSlot.get(componentToWake).release();
            } else {
                removeComponentFromIndexedDevice(componentId, sourceDeviceId);
                removeComponentFromDevice(componentId, sourceDeviceId);
            }
            mutex.release();
            transfer.prepare();
            previousPrepareSemaphore.release();
            deviceIndexedSlots.get(destinationDeviceId).get(myNewIndex).semaphore.acquire();

        } else { // There is a slot :)
            // Reserve our seat.
            addComponentToDevice(componentId, destinationDeviceId);
            int newIndex = reserveFreeSpotOnIndexedDevice(componentId, destinationDeviceId);
            // Free our place.
            ComponentId componentToWake = findReplacementComponent(sourceDeviceId);
            if (componentToWake != null) {
                componentIndex.put(componentToWake, myIndex);
                reserveSpotOnIndexedDevice(componentToWake, sourceDeviceId, myIndex);
                componentWaitingForSlot.get(componentToWake).release();
            } else {
                removeComponentFromIndexedDevice(componentId, sourceDeviceId);
                removeComponentFromDevice(componentId, sourceDeviceId);
            }
            mutex.release();
            transfer.prepare();
            previousPrepareSemaphore.release();
            deviceIndexedSlots.get(destinationDeviceId).get(newIndex).semaphore.acquire();
        }
        transfer.perform();
    }

    private void executeValidTransfer(final ComponentTransfer transfer) throws InterruptedException {
        final ComponentId componentId = transfer.getComponentId();
        final DeviceId sourceDeviceId = transfer.getSourceDeviceId();
        final DeviceId destinationDeviceId = transfer.getDestinationDeviceId();

        if (sourceDeviceId == null) {
            componentSemaphore.put(componentId, new Semaphore(1));
        }
        componentSemaphore.get(componentId).acquire();
        mutex.acquire();

        if (sourceDeviceId != null && destinationDeviceId != null) {
            // Transfer existing component.
            TransferComponentBetweenDevices(componentId, sourceDeviceId, destinationDeviceId, transfer);
        } else if (sourceDeviceId == null) {
            // Introducing new component to the system.
            transferNewComponentToDevice(componentId, destinationDeviceId, transfer);
        } else {
            // (destinationDeviceId == null) Delete component from the system.
            deleteComponentFromDevice(componentId, sourceDeviceId, transfer);
        }
        componentSemaphore.get(componentId).release();
    }
    @Override
    public void execute(ComponentTransfer transfer) throws TransferException {
        handleWrongTransfers(transfer);
        try {
            executeValidTransfer(transfer);
        } catch (InterruptedException e) {
            throw new RuntimeException(PANIC_UNEXPECTED_THREAD_INTERRUPTION);
        }

    }
}
