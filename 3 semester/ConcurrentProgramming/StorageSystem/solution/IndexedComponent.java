package cp2023.solution;

import cp2023.base.ComponentId;

import java.util.concurrent.Semaphore;

public class IndexedComponent {
    public ComponentId componentId;
    public int index;
    public Semaphore semaphore = new Semaphore(1);

    public IndexedComponent(ComponentId componentId, int index) {
        this.componentId = componentId;
        this.index = index;
    }
}
