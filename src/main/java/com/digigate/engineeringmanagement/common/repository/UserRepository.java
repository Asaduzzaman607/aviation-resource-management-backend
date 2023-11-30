package com.digigate.engineeringmanagement.common.repository;

import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.payload.response.UserViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UserProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UsernameProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.common.payload.response.UserViewModel(" +
            "u.id, " +
            "u.employeeId, " +
            "u.login, " +
            "u.role.id, " +
            "u.role.name, " +
            "e.name, " +
            "e.email, " +
            "e.officePhone, " +
            "e.officeMobile, " +
            "ds.name, " +
            "d.name, " +
            "s.name, " +
            "u.createdAt, " +
            "u.isActive" +
            ") " +
            "FROM User u inner join Employee e on u.employeeId = e.id " +
            "inner join Designation ds on e.designationId = ds.id " +
            "inner join Section  s on ds.sectionId = s.id " +
            "inner join Department  d on s.departmentId = d .id " +
            " WHERE " +
            "(u.id = :id)"
    )
    Optional<UserViewModel> findByUserId(@Param("id") Long id);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.common.payload.response.UserViewModel(" +
            "u.id, " +
            "u.employeeId, " +
            "u.login, " +
            "u.role.id, " +
            "u.role.name, " +
            "e.name, " +
            "e.email, " +
            "e.officePhone, " +
            "e.officeMobile, " +
            "ds.name, " +
            "d.name, " +
            "s.name, " +
            "u.createdAt, " +
            "u.isActive" +
            ") " +
            "FROM User u inner join Employee e on u.employeeId = e.id " +
            "inner join Designation ds on e.designationId = ds.id " +
            "inner join Section  s on ds.sectionId = s.id " +
            "inner join Department  d on s.departmentId = d .id " +
            " WHERE " +
            "(:login is null OR u.login LIKE :login%) AND " +
            "(:name is null OR e.name LIKE :name%) AND " +
            "(:isActive is null OR u.isActive = :isActive) AND " +
            "(:roleId is null OR u.role.id = :roleId) "
    )
    Page<UserViewModel> findBySearchCriteria(@Param("login") String login,
                                             @Param("name") String name,
                                             @Param("roleId") Integer roleId,
                                             @Param("isActive") Boolean isActive,
                                             Pageable pageable);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.common.payload.response.UserViewModel(" +
            "u.id, " +
            "u.employeeId, " +
            "u.login, " +
            "u.role.id, " +
            "u.role.name, " +
            "e.name, " +
            "e.email, " +
            "e.officePhone, " +
            "e.officeMobile, " +
            "ds.name, " +
            "d.name, " +
            "s.name, " +
            "u.createdAt, " +
            "u.isActive" +
            ") " +
            "FROM User u inner join Employee e on u.employeeId = e.id " +
            "inner join Designation ds on e.designationId = ds.id " +
            "inner join Section  s on ds.sectionId = s.id " +
            "inner join Department  d on s.departmentId = d .id " +
            " WHERE " +
            "u.isActive = true order by u.login asc")
    List<UserViewModel> findAllUser();
    UsernameProjection findUserById(Long id);
    Set<UsernameProjection> findUserByIdIn(Set<Long> idList);
    Set<UserProjection> findByIdIn(Set<Long> ids);
    List<UsernameProjection> findByIsActiveTrue();
    List<UsernameProjection> findByEmployeeIdIn(Set<Long> idList);
    List<User> findByEmployeeIdAndIsActiveTrue(Long employeeId);
    Optional<User> findByLoginAndIdNot(String login, Long id);
}