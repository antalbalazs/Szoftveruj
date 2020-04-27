package person;

import com.github.javafaker.Faker;
import person.model.Address;
import person.model.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.ZoneId;
import java.util.List;

public class Main {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-example");
    static Faker faker = new Faker();


    public static void main(String args[]){
        EntityManager em = emf.createEntityManager();
        int db = 1000;

        try {
            for (int i = 0; i < db; i++) {
                em.getTransaction().begin();
                em.persist(randomPerson());
                em.getTransaction().commit();
            }

            getPeople().forEach(System.out::println);
            deletePeople();

        } finally{
            em.close();
            emf.close();
        }
    }

    public static Person randomPerson(){
        Person person = Person.builder()
                .name(faker.name().fullName())
                .dob(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .gender(faker.options().option(Person.Gender.class))
                .address(randomAddress())
                .email(faker.internet().emailAddress())
                .profession(faker.company().profession())
                .build();
        return person;
    }

    public static Address randomAddress(){
        Address address = Address.builder()
                .country(faker.address().country())
                .state(faker.address().state())
                .city(faker.address().city())
                .streetAddress(faker.address().streetAddress())
                .zip(faker.address().zipCode())
                .build();
        return address;
    }

    private static void deletePeople() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            int count = em.createQuery("DELETE FROM Person").executeUpdate();
            System.out.println("Deleted "+ count +" people");
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private static List<Person> getPeople() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Person p ORDER BY p.id", Person.class).getResultList();
        } finally {
            em.close();
        }
    }
}
