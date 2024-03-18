package cp2023.cycleHandlerTests;


import cp2023.base.ComponentId;
import cp2023.base.DeviceId;
import cp2023.solution.CycleHandler;
import cp2023.solution.IndexedComponent;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.Semaphore;
import static org.junit.jupiter.api.Assertions.*;

public class CycleHandlerTest {

    @Test
    public void testCycleDetectionWithCycle() {
        // Create sample data with a cycle
        Map<ComponentId, DeviceId> componentDestination = new LinkedHashMap<>();
        componentDestination.put(new ComponentId(1), new DeviceId(102));
        componentDestination.put(new ComponentId(2), new DeviceId(103));
        componentDestination.put(new ComponentId(3), new DeviceId(104));
        componentDestination.put(new ComponentId(4), new DeviceId(101));

        Map<DeviceId, List<IndexedComponent>> deviceIndexedSlots = new HashMap<>();
        deviceIndexedSlots.put(new DeviceId(101), Arrays.asList(new IndexedComponent(new ComponentId(1), 0)));
        deviceIndexedSlots.put(new DeviceId(102), Arrays.asList(new IndexedComponent(new ComponentId(2), 0)));
        deviceIndexedSlots.put(new DeviceId(103), Arrays.asList(new IndexedComponent(new ComponentId(3), 0)));
        deviceIndexedSlots.put(new DeviceId(104), Arrays.asList(new IndexedComponent(new ComponentId(4), 0)));

        List<ComponentId> cycle = CycleHandler.detectCycle(new ComponentId(1), new DeviceId(101), componentDestination, deviceIndexedSlots);

        assertNotNull(cycle);
        assertEquals(4, cycle.size());
        assertEquals(new ComponentId(1), cycle.get(0));
        assertEquals(new ComponentId(2), cycle.get(1));
        assertEquals(new ComponentId(3), cycle.get(2));
        assertEquals(new ComponentId(4), cycle.get(3));
    }

    @Test
    public void testCycleDetectionWithoutCycle() {
        // Create sample data without a cycle
        Map<ComponentId, DeviceId> componentDestination = new LinkedHashMap<>();
        componentDestination.put(new ComponentId(10), new DeviceId(201));
        componentDestination.put(new ComponentId(20), new DeviceId(202));
        componentDestination.put(new ComponentId(30), new DeviceId(203));

        Map<DeviceId, List<IndexedComponent>> deviceIndexedSlots = new HashMap<>();
        deviceIndexedSlots.put(new DeviceId(201), Arrays.asList(new IndexedComponent(new ComponentId(10), 0)));
        deviceIndexedSlots.put(new DeviceId(202), Arrays.asList(new IndexedComponent(new ComponentId(20), 0)));
        deviceIndexedSlots.put(new DeviceId(203), Arrays.asList(new IndexedComponent(new ComponentId(30), 0)));

        List<ComponentId> cycle = CycleHandler.detectCycle(new ComponentId(10), new DeviceId(201), componentDestination, deviceIndexedSlots);

        assertNull(cycle);
    }
}


