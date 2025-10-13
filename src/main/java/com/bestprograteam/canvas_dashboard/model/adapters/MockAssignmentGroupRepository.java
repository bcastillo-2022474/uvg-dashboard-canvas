package com.bestprograteam.canvas_dashboard.model.adapters;

import com.bestprograteam.canvas_dashboard.model.entities.AssignmentGroup;
import com.bestprograteam.canvas_dashboard.model.repositories.AssignmentGroupRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock implementation of AssignmentGroupRepository for testing.
 * Returns hardcoded assignment group (category) data.
 */
@Repository
public class MockAssignmentGroupRepository implements AssignmentGroupRepository {

    private final Map<Integer, List<AssignmentGroup>> groupsByCourse;

    public MockAssignmentGroupRepository() {
        groupsByCourse = new HashMap<>();

        // Course 101 - Data Structures groups
        List<AssignmentGroup> course101Groups = new ArrayList<>();
        course101Groups.add(createGroup(1, 101, "Assignments", 1, 50.0));
        course101Groups.add(createGroup(2, 101, "Labs", 2, 20.0));
        course101Groups.add(createGroup(3, 101, "Exams", 3, 30.0));
        groupsByCourse.put(101, course101Groups);

        // Course 102 - OOP groups
        List<AssignmentGroup> course102Groups = new ArrayList<>();
        course102Groups.add(createGroup(4, 102, "Assignments", 1, 50.0));
        course102Groups.add(createGroup(5, 102, "Labs", 2, 20.0));
        course102Groups.add(createGroup(6, 102, "Project", 3, 30.0));
        groupsByCourse.put(102, course102Groups);

        // Course 103 - Database groups
        List<AssignmentGroup> course103Groups = new ArrayList<>();
        course103Groups.add(createGroup(7, 103, "Assignments", 1, 45.0));
        course103Groups.add(createGroup(8, 103, "Labs", 2, 25.0));
        course103Groups.add(createGroup(9, 103, "Project", 3, 30.0));
        groupsByCourse.put(103, course103Groups);

        // Course 104 - Software Engineering groups
        List<AssignmentGroup> course104Groups = new ArrayList<>();
        course104Groups.add(createGroup(10, 104, "Assignments", 1, 40.0));
        course104Groups.add(createGroup(11, 104, "Labs", 2, 20.0));
        course104Groups.add(createGroup(12, 104, "Project", 3, 40.0));
        groupsByCourse.put(104, course104Groups);
    }

    private AssignmentGroup createGroup(Integer id, Integer courseId, String name,
                                       Integer position, Double weight) {
        AssignmentGroup group = new AssignmentGroup();
        group.setId(id);
        group.setCourseId(courseId);
        group.setName(name);
        group.setPosition(position);
        group.setGroupWeight(weight);
        return group;
    }

    @Override
    public List<AssignmentGroup> findAssignmentGroupsByCourseId(Integer courseId) {
        return new ArrayList<>(groupsByCourse.getOrDefault(courseId, new ArrayList<>()));
    }

    @Override
    public AssignmentGroup findAssignmentGroupById(Integer courseId, Integer groupId) {
        List<AssignmentGroup> groups = groupsByCourse.get(courseId);
        if (groups == null) {
            return null;
        }
        return groups.stream()
                .filter(g -> g.getId().equals(groupId))
                .findFirst()
                .orElse(null);
    }
}
