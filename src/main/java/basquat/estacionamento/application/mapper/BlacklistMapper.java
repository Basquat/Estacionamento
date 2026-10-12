package basquat.estacionamento.application.mapper;

import basquat.estacionamento.User.BlacklistEntry;
import basquat.estacionamento.application.dto.request.BlacklistCreateRequest;
import basquat.estacionamento.application.dto.response.BlacklistResponse;
import java.util.UUID;

public class BlacklistMapper {

    public static BlacklistEntry toEntity(BlacklistCreateRequest request) {
        if (request == null) return null;

        BlacklistEntry entry = new BlacklistEntry();
        entry.setId(UUID.randomUUID().toString());
        entry.setPlaca(request.getPlaca());
        entry.setMotivo(request.getMotivo());
        entry.setFoto(request.getFoto());
        entry.setCriadoEm(System.currentTimeMillis());

        return entry;
    }

    public static BlacklistResponse toResponse(BlacklistEntry entity) {
        if (entity == null) return null;

        return new BlacklistResponse(
            entity.getId(),
            entity.getPlaca(),
            entity.getMotivo(),
            entity.getFoto(),
            entity.getCriadoEm()
        );
    }
}