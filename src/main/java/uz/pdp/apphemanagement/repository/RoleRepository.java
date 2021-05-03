package uz.pdp.apphemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.apphemanagement.entity.Role;
import uz.pdp.apphemanagement.entity.enums.RoleName;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleName(RoleName roleName); // find role by name
}
