package com.gooaein.goojilgoojil.repository;

import com.gooaein.goojilgoojil.dto.type.ERole;
import com.gooaein.goojilgoojil.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndRefreshTokenAndIsLogin(Long id, String refreshToken, Boolean isLogin);

    Optional<User> findBySessionId(String sessionId);

    Optional<User> findBySerialId(String serialId);
    Boolean existsBySerialId(String serialId);

    @Query("select u.id as id, u.role as role, u.password as password from User u where u.serialId = :serialId")
    Optional<UserSecurityForm> findSecurityFormBySerialId(String serialId);

    @Query("select u.id as id, u.role as role, u.password as password from User u where u.id = :id and u.isLogin = true")
    Optional<UserSecurityForm> findSecurityFormById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.refreshToken = :refreshToken, u.isLogin = :isLogin where u.id = :id")
    void updateRefreshTokenAndLoginStatus(Long id, String refreshToken, Boolean isLogin);

    interface UserSecurityForm {
        Long getId();
        ERole getRole();
        String getPassword();
        static UserSecurityForm invoke(User user) {
            return new UserSecurityForm() {
                @Override
                public Long getId() {
                    return user.getId();
                }

                @Override
                public ERole getRole() {
                    return user.getRole();
                }

                @Override
                public String getPassword() {
                    return user.getPassword();
                }
            };
        }
    }
}
