import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PolynomialConstant {

    static class Point {
        BigInteger x;
        BigInteger y;

        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    static BigInteger decode(String value, int base) {
        return new BigInteger(value, base);
    }

    static BigInteger findConstant(List<Point> points, int k) {
        BigInteger c = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger xi = points.get(i).x;
            BigInteger yi = points.get(i).y;

            BigInteger numerator = yi;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i == j) continue;

                BigInteger xj = points.get(j).x;
                numerator = numerator.multiply(xj.negate());
                denominator = denominator.multiply(xi.subtract(xj));
            }

            c = c.add(numerator.divide(denominator));
        }

        return c;
    }

    public static void main(String[] args) throws IOException {

        String json = new String(Files.readAllBytes(Paths.get("input.json")));

        int k = Integer.parseInt(
                json.replaceAll("(?s).*\"k\"\\s*:\\s*(\\d+).*", "$1")
        );

        Map<String, String[]> data = new HashMap<>();

        String[] blocks = json.split("\\},");
        for (String block : blocks) {

            if (block.contains("\"keys\"")) continue;
            if (!block.contains("base")) continue;

            String key = block.replaceAll("(?s).*\"(\\d+)\"\\s*:.*", "$1");
            String base = block.replaceAll("(?s).*\"base\"\\s*:\\s*\"(\\d+)\".*", "$1");
            String value = block.replaceAll("(?s).*\"value\"\\s*:\\s*\"([^\"]+)\".*", "$1");

            data.put(key, new String[]{base, value});
        }

        List<Point> points = new ArrayList<>();

        for (String key : data.keySet()) {
            BigInteger x = new BigInteger(key);
            int base = Integer.parseInt(data.get(key)[0]);
            BigInteger y = decode(data.get(key)[1], base);

            points.add(new Point(x, y));
        }

        points.sort(Comparator.comparing(p -> p.x));

        BigInteger c = findConstant(points, k);
        System.out.println("Constant c = " + c);
    }
}
