package com.cormontia.android.rotateyourscribble.matrix3d

class Mat44 {
    // Convention: a[row][column]
    private var elements: Array<DoubleArray> = Array(4) { DoubleArray(4) }

    constructor() {
        zero()
    }

    companion object {
        //TODO?- Replace with operator times?
        fun multiply(a: Mat44, b: Mat44): Mat44 {
            val res = Mat44()
            for (row in 0..3) {
                for (col in 0..3) {
                    res[row,col] = a.getRow(row) * b.getColumn(col)!!
                }
            }
            return res
        }
    }

    /** Fills the Matrix with zeroes.
     * It must be <CODE>final</CODE> because it is called from the Constructor.
     */
    fun zero() {
        for (r in 0..3) {
            for (c in 0..3) {
                elements[r][c] = 0.0
            }
        }
    }

    operator fun set(row: Int, column: Int, value: Double) {
        elements[row][column] = value
    }

    operator fun get(row: Int, column: Int): Double {
        return elements[row][column]
    }

    //TODO?~ See if we can turn this into an operator overload.
    /**
     * Determine <CODE>this * v</CODE>.
     */
    fun multiply(v: Vec4): Vec4 {
        val res = Vec4(0.0, 0.0, 0.0, 0.0)
        for (row in 0..3) {
            res[row] = getRow(row) * v
        }
        return res
    }

    /**
     * Determine <CODE>this * [0,0,0,1]</CODE>.
     */
    fun multiplyOrigin(): Vec4 {
        return Vec4(getRow(0)[3], getRow(1)[3], getRow(2)[3], getRow(3)[3])
    }

    fun getRow(row: Int): Vec4 {
        //TODO!+ Bounds check
        return Vec4(
            elements[row][0],
            elements[row][1],
            elements[row][2],
            elements[row][3]
        )
    }

    fun getColumn(col: Int): Vec4? {
        // TODO!+ Bounds check
        return Vec4(
            elements[0][col],
            elements[1][col],
            elements[2][col],
            elements[3][col]
        )
    }

    operator fun times(that: Mat44): Mat44 {
        val res: Mat44 = Mat44()
        for (row in 0..3) {
            for (col in 0..3) {
                res[row,col] = this.getRow(row) * that.getColumn(col)!!
            }
        }
        return res
    }

    /*
    // For debugging
    public void print( PrintStream ps )
    {
        for ( int row = 0; row < 4; row++ )
        {
            ps.println( );
            for ( int col = 0; col < 4; col++ )
            {
                ps.print( "\t" + elements[ row ][ col ] );
            }
        }
    }
    */
}