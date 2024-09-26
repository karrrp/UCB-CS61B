package capers;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

import static capers.Utils.*;

/** Represents a dog that can be serialized.
 * @author TODO
*/
public class Dog implements Serializable { // TODO

    /** Folder that dogs live in. */
    static final File DOG_FOLDER = new File(".capers\\dogs");

    /** Age of dog. */
    private int age;
    /** Breed of dog. */
    private String breed;
    /** Name of dog. */
    private String name;
    private static HashSet<String> nameSet = new HashSet<>();

    /**
     * Creates a dog object with the specified parameters.
     * @param name Name of dog
     * @param breed Breed of dog
     * @param age Age of dog
     */
    public Dog(String name, String breed, int age) {
        if (!nameSet.contains(name)) {
            nameSet.add(name);
        } else{
            throw new RuntimeException("the name must be UNIQUE");
        }
        this.age = age;
        this.breed = breed;
        this.name = name;
    }
    public static Boolean hasName(String name) {
        return nameSet.contains(name);
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name) {
        File tarDog = Utils.join(".capers","dogs",name);
        Dog serializableDog = readObject(tarDog, Dog.class);
        return serializableDog;
    }
    /**
     * Increases a dog's age and celebrates!
     */

    public void haveBirthday() {
        age += 1;
        this.saveDog();
        System.out.println(this);
        System.out.print("Happy birthday! Woof! Woof!");
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() {
        // TODO (hint: don't forget dog names are unique)
        File newDog = new File(DOG_FOLDER, name);
        try {
            newDog.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(newDog, this);
    }

    @Override
    public String toString() {
        return String.format(
            "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
            name, breed, age);
    }

}
