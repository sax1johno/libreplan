package org.navalplanner.business.test.planner.entities;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.navalplanner.business.orders.entities.HoursGroup;
import org.navalplanner.business.planner.entities.SpecificResourceAllocation;
import org.navalplanner.business.planner.entities.Task;
import org.navalplanner.business.planner.entities.TaskElement;

/**
 * @author Óscar González Fernández <ogonzalez@igalia.com>
 */
public class TaskTest {

    private Task task;
    private HoursGroup hoursGroup;

    public TaskTest() {
        hoursGroup = new HoursGroup();
        hoursGroup.setWorkingHours(3);
        task = Task.createTask(hoursGroup);
    }

    @Test
    public void taskIsASubclassOfTaskElement() {
        assertTrue(task instanceof TaskElement);
    }

    @Test
    public void taskHasHoursSpecifiedAtOrderComingFromItsHoursGroup() {
        assertThat(task.getHoursSpecifiedAtOrder(), equalTo(hoursGroup.getWorkingHours()));
    }

    @Test
    public void taskMustHaveOneHoursGroup() {
        HoursGroup hoursGroup = task.getHoursGroup();
        assertNotNull(hoursGroup);
    }

    public static TaskElement createValidTask() {
        HoursGroup hours = new HoursGroup();
        hours.setWorkingHours(20);
        return Task.createTask(hours);
    }

    @Test
    public void taskAddResourceAllocation() {
        assertThat(task.getResourceAllocations().size(), equalTo(0));

        SpecificResourceAllocation resourceAllocation = SpecificResourceAllocation.create(task);
        task.addResourceAllocation(resourceAllocation);

        assertThat(task.getResourceAllocations().size(), equalTo(1));
        assertThat(
                resourceAllocation.getTask().getResourceAllocations().size(),
                equalTo(1));
    }

    @Test
    public void taskRemoveResourceAllocation() {
        assertThat(task.getResourceAllocations().size(), equalTo(0));

        SpecificResourceAllocation resourceAllocation = SpecificResourceAllocation.create(task);
        task.addResourceAllocation(resourceAllocation);

        assertThat(task.getResourceAllocations().size(), equalTo(1));

        task.removeResourceAllocation(resourceAllocation);
        assertThat(task.getResourceAllocations().size(), equalTo(0));
    }

}