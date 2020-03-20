package com.example.registrar.boundary;

import com.example.addressbook.model.AddressBook;
import com.example.addressbook.model.Person;
import com.example.registrar.model.Registration;
import com.example.registrar.model.RegistrationContact;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Map;

@Path("/registrars")
@GraphQLApi
public class RegistrarBoundary {
    private static final Registration JANE_ORG = Registration.builder()
        .id("10001")
        .domain("jane.org")
        .admin(RegistrationContact.builder()
            .personId("1")
            .addressId("101")
            .email("jane.doe@example.com")
            .phoneNumber("+49721123456")
            .build())
        .build();

    private static final Map<String, Registration> REGISTRY = Map.of(
        JANE_ORG.getId(), JANE_ORG
    );

    @Inject AddressBook addressBook;

    @Path("/{id}")
    public @GET @Query Registration registration(@PathParam("id") String id) {
        return REGISTRY.get(id);
    }

    @SuppressWarnings("unused")
    public Person person(@Source Registration registration) {
        return addressBook.getPersonById(registration.getAdmin().getPersonId());
    }
}
