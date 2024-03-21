package org.benchmarker.bmcontroller.user.repository;

import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, String> {

}
