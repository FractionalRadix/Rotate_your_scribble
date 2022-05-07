package com.cormontia.android.rotateyourscribble.matrix3d

class Vec4(a: Double, b: Double, c: Double, d: Double) {
    private var data: DoubleArray = doubleArrayOf(a, b, c, d)

    //constructor(a: Double, b: Double, c: Double, d: Double)

    companion object {
        //TODO?- Use 'times' instead?
        fun multiply(a: Vec4, b: Vec4): Double {
            var res = 0.0
            for (i in 0..3) {
                res += a[i] * b[i]
            }
            return res
        }
    }

    operator fun get(index: Int): Double {
        //TODO!+ Bounds check
        return data[index]
    }

    operator fun set(index: Int, value: Double) {
        //TODO!+ Bounds check
        data[index] = value
    }

    operator fun times(that: Vec4): Double {
        var res = 0.0
        for (i in 0..3) {
            res += this[i] * that[i]
        }
        return res
    }
}