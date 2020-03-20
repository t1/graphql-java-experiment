package com.example.registrar.boundary;

import com.example.addressbook.model.AddressBook;
import com.example.addressbook.model.Person;
import com.example.registrar.model.Registration;
import com.example.registrar.model.RegistrationContact;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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
        Registration registration = REGISTRY.get(id);
        resolve(registration.getAdmin());
        return registration;
    }

    private void resolve(RegistrationContact contact) {
        Person person = addressBook.getPersonById(contact.getPersonId());
        contact.setPerson(person);
        contact.setAddress(find(person.getAddresses(), address -> address.getId().equals(contact.getAddressId())));
    }

    private static <T> T find(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).findAny().orElse(null);
    }

    // TODO this would be nice to use, but it throws a NPE in ReflectionDataFetcher...
    //  seems to be related to the arjuna scanning. Workaround: #resolve extended fields by hand
    // @SuppressWarnings("unused")
    // public Person person(@Source RegistrationContact contact) {
    //     return addressBook.getPersonById(contact.getPersonId());
    // }
}
