package ir.piana.dev.openidc.gateway.controller.ui;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Hidden
@ConditionalOnProperty(prefix = "oidc-ui.controller",
        name = "enabled",
        havingValue = "true")
@RestController
@RequestMapping("oidc-ui")
//@RequestMapping("${oidc-ui.controller.base-url:oidc-ui}")
public class OidcUIController {
    private final ResourceLoader resourceLoader;

    public OidcUIController(
            ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private static final Map<String, Asset> assetMap = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] files = resolver.getResources("assets/dist/oidc-web/browser/*.js");
            for (Resource file : files) {
                assetMap.put(file.getFilename(), new Asset(
                        file.getFilename(), file.getInputStream().readAllBytes(),
                        "application/javascript"));
            }

            String assetName = "index.html";
            Resource resource = resourceLoader.getResource("classpath:assets/dist/oidc-web/browser/" + assetName);
            assetMap.put(assetName, new Asset(
                    assetName, resource.getInputStream().readAllBytes(), "text/html"));
            assetName = "favicon.ico";
            resource = resourceLoader.getResource("classpath:assets/dist/oidc-web/browser/" + assetName);
            assetMap.put(assetName, new Asset(
                    assetName, resource.getInputStream().readAllBytes(), "image/x-icon"));
            assetName = "styles-KTM7TO3R.css";
            resource = resourceLoader.getResource("classpath:assets/dist/oidc-web/browser/" + assetName);
            assetMap.put(assetName, new Asset(
                    assetName, resource.getInputStream().readAllBytes(), "text/css"));
            assetName = "media/bootstrap-icons-OCU552PF.woff";
            resource = resourceLoader.getResource("classpath:assets/dist/oidc-web/browser/" + assetName);
            assetMap.put(assetName.split("/")[1], new Asset(
                    assetName.split("/")[1], resource.getInputStream().readAllBytes(), "font/woff2"));
            assetName = "media/bootstrap-icons-X6UQXWUS.woff2";
            resource = resourceLoader.getResource("classpath:assets/dist/oidc-web/browser/" + assetName);
            assetMap.put(assetName.split("/")[1], new Asset(
                    assetName.split("/")[1], resource.getInputStream().readAllBytes(), "font/woff2"));
            /*assetName = "color-modes.js";
            resource = resourceLoader.getResource("classpath:assets/dist/oidc-web/browser/" + assetName);
            assetMap.put(assetName, new Asset(
                    assetName, resource.getInputStream().readAllBytes(), "application/javascript"));*/

            /*String i = """
                    main-AJDG4P5Y.js
                    polyfills-FFHMD2TL.js
                    scripts-EEEIPNC3.js
                    chunk-3LIMAG5K.js
                    chunk-3TS3DFA3.js
                    chunk-4FJIM32L.js
                    chunk-5GOU5Q6J.js
                    chunk-AGYOEUCJ.js
                    chunk-DNO4QXZW.js
                    chunk-GVF22ZTW.js
                    chunk-J47SELX5.js
                    chunk-JM66X7RX.js
                    chunk-LL4RZ7NH.js
                    chunk-LNF4ZT3S.js
                    chunk-MPDB6UA5.js
                    chunk-N3QDUTQH.js
                    chunk-QWZCORDQ.js
                    chunk-TYICYLY2.js
                    chunk-Y3EIMRQE.js
                    """;
            String[] split = i.split("\\r?\\n");

            Arrays.stream(split).forEach(asset -> {
                try {
                    Resource resource1 = resourceLoader.getResource(
                            "classpath:assets/dist/oidc-web/browser/" + asset.trim());
                    assetMap.put(asset.trim(), new Asset(
                            asset.trim(), resource1.getInputStream().readAllBytes(),
                            "application/javascript"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });*/
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(
            path = {"ui", "ui/"}
    )
    public void getAssetRoot(HttpServletResponse servletResponse) throws IOException {
        this.generateResponse(servletResponse, (Asset) assetMap.get("index.html"));
    }

    @GetMapping(path = {"ui/{asset-name}", "ui/media/{asset-name}"})
    public void getAsset(@PathVariable("asset-name") String assetName, HttpServletResponse servletResponse)
            throws IOException {
        if (!assetMap.containsKey(assetName.substring(0,
                assetName.indexOf("?") > 0 ?
                        assetName.indexOf("?") : assetName.length()))) {
            noContent(servletResponse);
            return;
        }
        generateResponse(servletResponse, assetMap.get(assetName));
    }

    private void generateResponse(HttpServletResponse servletResponse, Asset asset) throws IOException {
        servletResponse.setStatus(200);
        servletResponse.setContentType(asset.contentType);
        servletResponse.getOutputStream().write(asset.bytes);
    }

    private void noContent(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setStatus(404);
        servletResponse.getOutputStream().print("Asset Not Exist!");
    }

    record Asset(String name, byte[] bytes, String contentType) {
    }
}
