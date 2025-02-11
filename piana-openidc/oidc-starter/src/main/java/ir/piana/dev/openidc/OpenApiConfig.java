package ir.piana.dev.openidc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "OIDC Gateway APIs",
                version = "1.0"
        ),
        servers = {
                /*@Server(
                        description = "production",
                        url = "https://discrepancy-detection.jibit.ir/ddg"
                ),*/
                @Server(
                        description = "develop",
                        url = "http://localhost:8081"
                )
        },
        security = {
//                @SecurityRequirement(name = "bearerAuth")
        }

)

@SecuritySchemes(
        {
                @SecurityScheme(
                        name = "bearerAuth",
                        description = "JWT Auth authentication",
                        scheme = "bearer",
                        type = SecuritySchemeType.HTTP,
                        bearerFormat = "JWT",
                        in = SecuritySchemeIn.HEADER
                )
        }

)
public class OpenApiConfig {
}
