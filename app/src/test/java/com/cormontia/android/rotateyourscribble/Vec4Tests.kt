package com.cormontia.android.rotateyourscribble

import com.cormontia.android.rotateyourscribble.matrix3d.Vec4
import org.junit.Test

class Vec4Tests {
    @Test
    fun multiply_two_nonzero_vectors_results_in_their_product() {
        val v1 = Vec4( 3.0, 2.0, 1.0, 2.0)
        val v2 = Vec4( 4.0, 3.0, 2.0, 3.0)
        val product = v1 * v2
        assert(product == 26.0)
    }

    @Test
    fun multiply_with_zero_vector_resuls_in_zero() {
        val v1 = Vec4( 3.0, 2.0, 1.0, 2.0)
        val v2 = Vec4( 0.0, 0.0, 0.0, 0.0)
        val product = v1 * v2
        assert(product == 0.0)
    }
}