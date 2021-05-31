package com.kmatheis.vet.docs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kmatheis.vet.entity.User;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

@Configuration
@OpenAPIDefinition
public class ListDocConfiguration {
	@Bean
	public OpenAPI customOpenAPI() {
        @SuppressWarnings( "unchecked" )
		Schema<List<User>> newUserSchema = new Schema<List<User>>()
        		.addProperties( "[ * ]", new ObjectSchema() );  // TODO: this is not quite what we want

        return new OpenAPI()
                .info( new Info()
                        .title( "Your app title" )
                        .description( "App description" )
                        .version( "1.0" )
                        .license( new License().name( "GNU/GPL" ).url( "https://www.gnu.org/licenses/gpl-3.0.html" ) )
                )
                .components( new Components()
                        .addSchemas( "UserList" , newUserSchema )
                );
    }
}
