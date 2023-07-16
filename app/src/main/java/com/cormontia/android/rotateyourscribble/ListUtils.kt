package com.cormontia.android.rotateyourscribble

/**
 * Given a list, filter out the subsequent doubles - but <em>only</em> the subsequent ones.
 * For example: [1,1,1,2,2,3,1] would result in [1,2,3,1]. The first three "1"'s in the list are subsequent; the last one is not.
 * @return The list received, but with any subsequent values collapsed into a single occurrence.
 */
fun <T> List<T>.removeSubsequentDoubles(): List<T> {
    val result = mutableListOf<T>()

    if (isEmpty()) {
        return result
    }

    if (size == 1) {
        result.add(this[0])
        return result
    }

    var curVal = this[0]
    result.add(this[0])
    for (idx in 1 until size) {
        val nextVal = this[idx]
        if (nextVal != curVal) {
            result.add(nextVal)
            curVal = nextVal
        }
    }

    return result
}
