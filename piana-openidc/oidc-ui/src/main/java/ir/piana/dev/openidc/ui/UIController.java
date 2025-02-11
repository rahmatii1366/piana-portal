package ir.piana.dev.openidc.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.dev.openidc.ui.model.MenuModel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UIController {
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    private MenuModel menuModel = null;

    @PostConstruct
    public void init() {
        Resource resource = resourceLoader.getResource("classpath:data/menu.json");
        try {
            menuModel = objectMapper.readValue(
                    resource.getInputStream(), MenuModel.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @GetMapping({"/"})
    public String hello(Model model) {
        model.addAttribute("moduleStylePaths", Arrays.asList(
                "/crp-ng-login/styles-5INURTSO.css"
        ));
        model.addAttribute("moduleScriptPaths", Arrays.asList(
                "/crp-ng-login/polyfills-FFHMD2TL.js",
                "/crp-ng-login/main-N2NMVMM7.js"
        ));

        model.addAttribute("message", "Hello World!");

        model.addAttribute("menu", menuModel);
//        model.addAttribute("contatos", Arrays.asList("a", "b", "c", "d"));
        return "layout/panel-app";
//        return "layout/index-app";
    }

    @GetMapping({"/login"})
    public String login(Model model) {
        model.addAttribute("moduleStylePaths", Arrays.asList(
                "/crp-ng-login/styles-5INURTSO.css"
        ));
        model.addAttribute("moduleScriptPaths", Arrays.asList(
                "/crp-ng-login/polyfills-FFHMD2TL.js",
                "/crp-ng-login/main-N2NMVMM7.js"
        ));
        return "layout/login-app";
    }
}
