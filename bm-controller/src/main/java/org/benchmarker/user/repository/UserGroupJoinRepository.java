package org.benchmarker.user.repository;

import java.util.List;
import java.util.Optional;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.model.UserGroupJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserGroupJoinRepository extends JpaRepository<UserGroupJoin, Long> {

    List<UserGroupJoin> findByUserId(String userId);

    Optional<UserGroupJoin> findByUserAndUserGroup(User user, UserGroup userGroup);

    @Query("SELECT ugj FROM UserGroupJoin ugj WHERE ugj.userGroup.id = :groupId")
    List<UserGroupJoin> findByUserGroupId(@Param("groupId") String groupId);

    @Query("SELECT ugj FROM UserGroupJoin ugj WHERE ugj.user.id = :userId AND ugj.userGroup.id = :groupId")
    Optional<UserGroupJoin> findByUserIdAndUserGroupId(@Param("userId") String userId, @Param("groupId") String groupId);

    @Modifying
    @Query("DELETE FROM UserGroupJoin ugj WHERE ugj.user.id = :userId AND ugj.userGroup.id = :groupId")
    void deleteAllByUserIdAAndUserGroupId(@Param("userId") String userId, @Param("groupId") String groupId);

    @Modifying
    @Query("DELETE FROM UserGroupJoin ugj WHERE ugj.user.id = :userId")
    void deleteAllByUserId(@Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM UserGroupJoin ugj WHERE ugj.userGroup.id = :groupId")
    void deleteAllByUserGroupId(@Param("groupId") String groupId);
}
