/*
 * This file is part of ###PROJECT_NAME###
 *
 * Copyright (C) 2009 Fundación para o Fomento da Calidade Industrial e
 *                    Desenvolvemento Tecnolóxico de Galicia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.navalplanner.business.planner.entities.allocationalgorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.joda.time.LocalDate;
import org.navalplanner.business.planner.entities.DayAssignment;
import org.navalplanner.business.planner.entities.GenericResourceAllocation;
import org.navalplanner.business.planner.entities.ResourceAllocation;
import org.navalplanner.business.planner.entities.ResourcesPerDay;
import org.navalplanner.business.planner.entities.SpecificResourceAllocation;
import org.navalplanner.business.resources.entities.Resource;

public abstract class AllocationBeingModified {

    private static class GenericAllocationBeingModified extends
            AllocationBeingModified {
        private final GenericResourceAllocation genericAllocation;

        GenericAllocationBeingModified(
                GenericResourceAllocation resourceAllocation,
                ResourcesPerDay resourcesPerDay,
                Collection<? extends Resource> resources) {
            super(resourceAllocation, resourcesPerDay, resources);
            Validate.isTrue(!resources.isEmpty());
            this.genericAllocation = resourceAllocation;
        }

        @Override
        public void applyAllocationOnAllTaskLength() {
            genericAllocation.forResources(getResources()).allocate(getGoal());
        }

        @Override
        public void applyAllocationUntil(LocalDate endExclusive) {
            genericAllocation.forResources(getResources()).until(endExclusive)
                    .allocate(getGoal());
        }

        @Override
        public List<DayAssignment> createAssignmentsAtDay(LocalDate day,
                int limit) {
            return genericAllocation.createAssignmentsAtDay(getResources(),
                    day, getGoal(), limit);
        }
    }

    private static class SpecificAllocationBeingModified extends
            AllocationBeingModified {

        private final SpecificResourceAllocation resourceAllocation;

        SpecificAllocationBeingModified(
                SpecificResourceAllocation resourceAllocation,
                ResourcesPerDay resourcesPerDay,
                Collection<? extends Resource> resources) {
            super(resourceAllocation, resourcesPerDay, resources);
            this.resourceAllocation = resourceAllocation;
        }

        @Override
        public void applyAllocationOnAllTaskLength() {
            resourceAllocation.allocate(getGoal());
        }

        @Override
        public void applyAllocationUntil(LocalDate endExclusive) {
            resourceAllocation.until(endExclusive).allocate(getGoal());
        }

        @Override
        public List<DayAssignment> createAssignmentsAtDay(LocalDate day,
                int limit) {
            return resourceAllocation.createAssignmentsAtDay(day, getGoal(),
                    limit);
        }
    }

    public static AllocationBeingModified create(
            GenericResourceAllocation resourceAllocation,
            ResourcesPerDay resourcesPerDay, List<Resource> resources) {
        return new GenericAllocationBeingModified(resourceAllocation,
                resourcesPerDay, resources);
    }

    public static AllocationBeingModified create(
            SpecificResourceAllocation resourceAllocation,
            ResourcesPerDay resourcesPerDay) {
        return new SpecificAllocationBeingModified(resourceAllocation,
                resourcesPerDay, Collections.singletonList(resourceAllocation
                        .getResource()));
    }

    public static List<AllocationBeingModified> fromExistent(
            Collection<? extends ResourceAllocation<?>> allocations) {
        List<AllocationBeingModified> result = new ArrayList<AllocationBeingModified>();
        for (ResourceAllocation<?> resourceAllocation : allocations) {
            ResourcesPerDay perDay = resourceAllocation
                    .getResourcesPerDay();
            Validate.notNull(perDay);
            result.add(resourceAllocation.asAllocationBeingModified());
        }
        return result;
    }

    private final ResourceAllocation<?> beingModified;

    private final ResourcesPerDay goal;

    private final List<Resource> resourcesOnWhichApplyAllocation;

    private AllocationBeingModified(
            ResourceAllocation<?> resourceAllocation,
            ResourcesPerDay resourcesPerDay,
            Collection<? extends Resource> resources) {
        this.beingModified = resourceAllocation;
        this.goal = resourcesPerDay;
        this.resourcesOnWhichApplyAllocation = Collections
                .unmodifiableList(new ArrayList<Resource>(
                resources));
    }

    public ResourceAllocation<?> getBeingModified() {
        return beingModified;
    }

    public ResourcesPerDay getGoal() {
        return goal;
    }

    public static List<ResourceAllocation<?>> stripResourcesPerDay(
            Collection<AllocationBeingModified> withResourcesPerDay) {
        List<ResourceAllocation<?>> result = new ArrayList<ResourceAllocation<?>>();
        for (AllocationBeingModified r : withResourcesPerDay) {
            result.add(r.getBeingModified());
        }
        return result;
    }

    public List<Resource> getResources() {
        return resourcesOnWhichApplyAllocation;
    }

    public abstract void applyAllocationOnAllTaskLength();

    public abstract void applyAllocationUntil(LocalDate endExclusive);

    public abstract List<DayAssignment> createAssignmentsAtDay(LocalDate day,
            int limit);

}
