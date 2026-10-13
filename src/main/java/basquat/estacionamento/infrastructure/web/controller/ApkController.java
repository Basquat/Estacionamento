package basquat.estacionamento.infrastructure.web.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Entrega o APK com o Content-Type correto. O GitHub serve o asset como
 * octet-stream e alguns navegadores de celular salvam como .zip.
 */
@RestController
public class ApkController {

    private static final URI APK_URL = URI.create(
            "https://github.com/Basquat/Estacionamento/releases/download/apk-latest/Estaciona.apk");

    // Criado só no primeiro download: uma falha aqui nunca pode impedir o app de subir.
    private volatile HttpClient http;

    private HttpClient http() {
        if (http == null) {
            http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        }
        return http;
    }

    @GetMapping("/baixar-apk")
    public ResponseEntity<byte[]> baixar() throws Exception {
        HttpResponse<byte[]> r = http().send(HttpRequest.newBuilder(APK_URL).build(),
                HttpResponse.BodyHandlers.ofByteArray());
        if (r.statusCode() != 200) {
            return ResponseEntity.status(502).build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.android.package-archive")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("Estaciona.apk").build().toString())
                .contentLength(r.body().length)
                .body(r.body());
    }
}
