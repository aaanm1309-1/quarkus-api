package com.adrianomenezes.quarkussocial;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(
        info = @Info(
                title = "Api quarkus social",
                version = "1.0",
                contact = @Contact(
                        name = "Adriano Menezes",
                        url = "http://adriano_menezes.com.br",
                        email = "adriano@adrianomenezes.com.br"
                ),
                license = @License(
                        name = "Apache",
                        url = "teste"
                )
        )
)
public class QuarkusSocialApplication extends Application {
}
