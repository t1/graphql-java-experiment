package com.example.registrar.boundary;

import com.example.addressbook.boundary.AddressBookBoundary;
import com.example.addressbook.model.Address;
import com.example.addressbook.model.Person;
import com.example.registrar.model.Contact;
import com.example.registrar.model.Registration;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Map;

@Path("/registrars")
@GraphQLApi
public class RegistrarBoundary {
    private static final Registration JANE_ORG = Registration.builder()
        .id("10001")
        .domain("jane.org")
        .admin(Contact.builder()
            .personId("1")
            .addressId("101")
            .email("jane.doe@example.com")
            .phoneNumber("+49721123456")
            .build())
        .build();

    private static final Map<String, Registration> REGISTRY = Map.of(
        JANE_ORG.getId(), JANE_ORG
    );

    @Path("/{id}")
    public @GET @Query Registration registration(@PathParam("id") String id) {
        Registration registration = REGISTRY.get(id);
        resolve(registration.getAdmin());
        return registration;
    }

    private void resolve(Contact contact) {
        Person person = AddressBookBoundary.REPOSITORY.get(contact.getPersonId());
        contact.setPerson(person);
        contact.setAddress(find(contact.getAddressId(), person.getAddresses()));
    }

    private Address find(String addressId, List<Address> addresses) {
        return addresses.stream().filter(address -> address.getId().equals(addressId)).findAny()
            .orElse(null);
    }

    // TODO this would be nice to use, but it throws a NPE in ReflectionDataFetcher...
    //  seems to be related to the arjuna scanning. Workaround: #resolve extended fields by hand
    // @SuppressWarnings("unused")
    // public static Person person(@Source Contact contact) {
    //     return AddressBookBoundary.REPOSITORY.get(contact.getPersonId());
    // }
}
