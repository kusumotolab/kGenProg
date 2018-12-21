package example;

public class QuickSort {

  public void quicksort(int[] value) {
    int left = 0;
    int right = value.length - 1;
    quicksort(value, left, right);
  }

  /**
   * 無限ループするバグを含むメソッド
   * 
   * @param value
   * @param left
   * @param right
   */
  private void quicksort(int[] value, int left, int right) {

    int i = left;
    int j = right;
    int pivot = value[(left + right) / 2];

    while (true) {
      while (value[i] < pivot)
        j++; // to be "i++"
      while (pivot < value[j])
        j--;
      if (i >= j)
        break;
      swap(value, i, j);
      i++;
      j--;
    }

    if (left < i - 1)
      quicksort(value, left, i - 1);
    if (j + 1 < right)
      quicksort(value, j + 1, right);
  }

  private void swap(int[] value, int i, int j) {
    int tmp = value[i];
    value[i] = value[j];
    value[j] = tmp;
  }

  @SuppressWarnings("unused")
  private void reuse_me() {
    int i = 0;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
    i++;
  }
}
