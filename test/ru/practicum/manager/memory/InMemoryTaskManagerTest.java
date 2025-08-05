package ru.practicum.manager.memory;

import ru.practicum.manager.TaskManagerTest;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}

