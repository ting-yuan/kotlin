module kotlin.stdlib.jdk9 {
    requires transitive kotlin.stdlib;
    requires kotlin.stdlib.jdk8;

    opens kotlin.internal.jdk9 to kotlin.stdlib;
}
