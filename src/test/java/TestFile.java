import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TestFile {

    @Test
    public void tryKW() {
        int[] arr = null;
        while ((arr = getCombinations(5, 2, arr)) != null) {
            System.out.println(Arrays.toString(arr));
        }
    }

    private int[] getCombinations(int n, int k, int[] arr) {
        if (arr == null) {
            arr = new int[k];
            for (int i = 0; i < k; i++) {
                arr[i] = i + 1;
            }
            return arr;
        }
        for (int i = k - 1; i >= 0; i--) {
            if (arr[i] < n - k + i + 1) {
                arr[i]++;
                for (int j = i; j < k - 1; j++) {
                    arr[j + 1] = arr[j] + 1;
                }
                return arr;
            }
        }
        return null;
    }
}
