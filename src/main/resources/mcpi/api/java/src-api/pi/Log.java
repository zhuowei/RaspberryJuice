package pi;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
class Log {

    static void debug(String s) {
        if (false) {
            System.out.println(s);
        }
    }

    static void info(String s) {
        System.out.println(s);
    }

    static void error(String s) {
        System.err.println(s);
    }
}
