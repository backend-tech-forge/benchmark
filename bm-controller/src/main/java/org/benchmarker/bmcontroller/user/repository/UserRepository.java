package org.benchmarker.bmcontroller.user.repository;

import org.benchmarker.bmcontroller.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}

