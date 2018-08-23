// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.linalg;

/** This class implements upper triangular matrices of linear algebra.

 * @version $Id$
 * @author L. Maisonobe

 */

public class UpperTriangularMatrix
  extends SquareMatrix {

  /** Simple constructor.
   * This constructor builds a upper triangular matrix of specified order, all
   * elements being zeros.
   * @param order order of the matrix
   */
  public UpperTriangularMatrix(int order) {
    super(order);
  }

  /** Simple constructor.
   * Build a matrix with specified elements.
   * @param order order of the matrix
   * @param data table of the matrix elements (stored row after row)
   */
  public UpperTriangularMatrix(int order, double[] data) {
    super(order, data);
  }

  /** Copy constructor.
   * @param u upper triangular matrix to copy
   */
  public UpperTriangularMatrix(UpperTriangularMatrix u) {
    super(u);
  }

  public Matrix duplicate() {
    return new UpperTriangularMatrix(this);
  }

  public void setElement(int i, int j, double value) {
    if (i > j) {
      throw new ArrayIndexOutOfBoundsException("cannot set elements"
                                               + " below diagonal of a"
                                               + " upper triangular matrix");
    }
    super.setElement(i, j, value);
  }

  /** Add a matrix to the instance.
   * This method adds a matrix to the instance. It does modify the instance.
   * @param u upper triangular matrix to add
   * @exception IllegalArgumentException if there is a dimension mismatch
   */
  public void selfAdd(UpperTriangularMatrix u) {

    // validity check
    if ((rows != u.rows) || (columns != u.columns)) {
      throw new IllegalArgumentException("cannot add a "
                                         + u.rows + 'x' + u.columns
                                         + " matrix to a "
                                         + rows + 'x' + columns
                                         + " matrix");
    }

    // addition loop
    for (int i = 0; i < rows; ++i) {
      for (int index = i * (columns + 1); index < (i + 1) * columns; ++index) {
        data[index] += u.data[index];
      }
    }

  }

  /** Substract a matrix from the instance.
   * This method substract a matrix from the instance. It does modify the instance.
   * @param u upper triangular matrix to substract
   * @exception IllegalArgumentException if there is a dimension mismatch
   */
  public void selfSub(UpperTriangularMatrix u) {

    // validity check
    if ((rows != u.rows) || (columns != u.columns)) {
      throw new IllegalArgumentException("cannot substract a "
                                         + u.rows + 'x' + u.columns
                                         + " matrix from a "
                                         + rows + 'x' + columns
                                         + " matrix");
    }

    // substraction loop
    for (int i = 0; i < rows; ++i) {
      for (int index = i * (columns + 1); index < (i + 1) * columns; ++index) {
        data[index] -= u.data[index];
      }
    }

  }

  public double getDeterminant(double epsilon) {
    double determinant = data[0];
    for (int index = columns + 1; index < columns * columns; index += columns + 1) {
      determinant *= data[index];
    }
    return determinant;
  }

  public Matrix solve(Matrix b, double epsilon)
    throws SingularMatrixException {
    // validity check
    if (b.getRows() != rows) {
      throw new IllegalArgumentException("dimension mismatch");
    }

    // prepare the data storage
    int bRows  = b.getRows();
    int bCols  = b.getColumns();

    double[] resultData = new double[bRows * bCols];
    int resultIndex     = bRows * bCols - 1;
    int lowerElements   = 0;
    int upperElements   = 0;
    int minJ            = columns;
    int maxJ            = 0;

    // solve the linear system
    for (int i = rows - 1; i >= 0; --i) {
      double diag = data[i * (columns + 1)];
      if (Math.abs(diag) < epsilon) {
        throw new SingularMatrixException();
      }
      double inv = 1.0 / diag;

      NonNullRange range = b.getRangeForRow(i);
      minJ = Math.min(minJ, range.begin);
      maxJ = Math.max(maxJ, range.end);

      int j = bCols - 1;
      while (j >= maxJ) {
        resultData[resultIndex] = 0.0;
        --resultIndex;
        --j;
      }

      // compute the possibly non null elements
      int bIndex = i * bCols + maxJ - 1;
      while (j >= minJ) {

        // compute the current element
        int index1 = (i + 1) * columns - 1;
        int index2 = (bRows - 1) * bCols + j;
        double value = b.data[bIndex];
        while (index1 >= i * (columns + 1)) {
          value -= data[index1] * resultData[index2];
          --index1;
          index2 -= bCols;
        }
        value *= inv;
        resultData[resultIndex] = value;

        // count the affected upper and lower elements
        // (in order to deduce the shape of the resulting matrix)
        if (j < i) {
          ++lowerElements;
        } else if (i < j) {
          ++upperElements;
        }

        --bIndex;
        --resultIndex;
        --j;

      }

      while (j >= 0) {
        resultData[resultIndex] = 0.0;
        --resultIndex;
        --j;
      }

    }

    return MatrixFactory.buildMatrix(bRows, bCols, resultData,
                                     lowerElements, upperElements);

  }

  public NonNullRange getRangeForRow(int i) {
    return new NonNullRange (i, columns);
  }

  public NonNullRange getRangeForColumn(int j) {
    return new NonNullRange (0, j + 1);
  }

  private static final long serialVersionUID = -197266611942032237L;

}
