package org.benchmarker.user.repository;

import java.util.List;
import java.util.Optional;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.model.UserGroupJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserGroupJoinRepository extends JpaRepository<UserGroupJoin, Long>{

    List<UserGroupJoin> findByUserId(String userId);
    Optional<UserGroupJoin> findByUserAndUserGroup(User user, UserGroup userGroup);
    @Query("SELECT ugj FROM UserGroupJoin ugj WHERE ugj.userGroup.id = :groupId")
    List<UserGroupJoin> findByUserGroupId(String groupId);
    @Query("SELECT ugj FROM UserGroupJoin ugj WHERE ugj.user.id = :userId AND ugj.userGroup.id = :groupId")
    Optional<UserGroupJoin> findByUserIdAndUserGroupId(String userId, String groupId);
    void deleteByUserId(String userId);

}
