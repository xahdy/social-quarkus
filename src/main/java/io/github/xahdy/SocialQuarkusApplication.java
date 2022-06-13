package io.github.xahdy;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(
        info = @Info(
                title = "API Social Quarkus",
                version = "1.0",
                contact = @Contact(
                        name = "Marlon Schemberger",
                        url = "https://github.com/xahdy/social-quarkus",
                        email = "marlon.schemberger@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LIUCENSE-2.0.html"
                )
        )
)
public class SocialQuarkusApplication extends Application {
}
