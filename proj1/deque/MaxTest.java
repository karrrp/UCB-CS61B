package deque;

import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;


public class MaxTest<Hexu> {
/*    @Test
    public void SizeMaxtest(){
        MaxArrayDeque<Integer> sizeMax = new MaxArrayDeque<>();
        sizeMax.addFirst(1);
        sizeMax.addFirst(2);
        sizeMax.addFirst(3);
        int max1 = sizeMax.max();
        assertEquals(3, max1);
    }*/

    public static class Person implements Comparable<Person>{
        public int age;
        public String id;
        public Person(int a, String c){
            age = a;
            id = c;
        }

        @Override
        public int compareTo(Person o) {
            return this.age - o.age;
        }
    }
    public class age_comparated implements Comparator<Person>{
        @Override
        public int compare(Person o1, Person o2) {
            return o1.compareTo(o2);
        }
    }

    public class ID_comparated implements Comparator<Person>{
        @Override
        public int compare(Person o1, Person o2) {
            return o1.id.compareTo(o2.id);
        }
    }
    @Test
    public void string_test(){
        Person p1 =new Person(18,"A");
        Person p2 =new Person(28,"B");
        Person p3 =new Person(8,"C");

        age_comparated a = new age_comparated();
        MaxArrayDeque<Person> ageMax = new MaxArrayDeque<>(a);
        ageMax.addFirst(p2);
        ageMax.addFirst(p3);
        ageMax.addFirst(p1);
        assertEquals(28, ageMax.max().age);
        Comparator<Person> Comparator1 = new ID_comparated();
        assertEquals(8,ageMax.max(Comparator1).age);
    }
}
