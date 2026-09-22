package basquat.estacionamento.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlacklistRepository extends JpaRepository<BlacklistEntry, String> {

    List<BlacklistEntry> findAllByOrderByCriadoEmDesc();
}
