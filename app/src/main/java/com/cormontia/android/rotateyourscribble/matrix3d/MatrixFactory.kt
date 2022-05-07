package com.cormontia.android.rotateyourscribble.matrix3d

import kotlin.math.cos
import kotlin.math.sin

class MatrixFactory
{
    companion object {
        fun Identity(): Mat44 {
            val m = Mat44()
            for (i in 0..3) {
                m[i, i] = 1.0
            }
            return m
        }

        fun Scale(Sx: Double, Sy: Double, Sz: Double): Mat44 {
            val m = Mat44()
            m[0, 0] = Sx
            m[1, 1] = Sy
            m[2, 2] = Sz
            m[3, 3] = 1.0
            return m
        }

        fun Translate(Tx: Double, Ty: Double, Tz: Double): Mat44 {
            val m = Identity()
            m[0, 3] = Tx
            m[1, 3] = Ty
            m[2, 3] = Tz
            m[3, 3] = 1.0
            return m
        }

        fun RotateAroundX(angle: Double): Mat44 {
            val m = Mat44()
            val ca = cos(angle)
            val sa = sin(angle)
            m[0, 0] = 1.0
            m[1, 1] = ca
            m[1, 2] = -sa
            m[2, 1] = sa
            m[2, 2] = ca
            m[3, 3] = 1.0
            return m
        }

        fun RotateAroundY(angle: Double): Mat44 {
            val m = Mat44()
            val ca = cos(angle)
            val sa = sin(angle)
            m[0, 0] = ca
            m[0, 2] = sa
            m[1, 1] = 1.0
            m[2, 0] = -sa
            m[2, 2] = ca
            m[3, 3] = 1.0
            return m
        }

        fun RotateAroundZ(angle: Double): Mat44 {
            val m = Mat44()
            val ca = cos(angle)
            val sa = sin(angle)
            m[0, 0] = ca
            m[0, 1] = -sa
            m[1, 0] = sa
            m[1, 1] = ca
            m[2, 2] = 1.0
            m[3, 3] = 1.0
            return m
        }

        fun RotateAroundAxis(startOfAxis: Vec4, endOfAxis: Vec4, angle: Double): Mat44 {
            // From: "Robotics: Control, Sensing, Vision and Intelligence", K.S. Fu, R.C. Gonzalez, C.S.G. Lee,
            // McGraw-Hill publishers, 1987.

            //TODO!+ Exceptional case if startOfAxis==endOfAxis
            // That is, everything that makes Plength == 0.0....
            val Px = endOfAxis[0] - startOfAxis[0]
            val Py = endOfAxis[1] - startOfAxis[1]
            val Pz = endOfAxis[2] - startOfAxis[2]
            val Plength = Math.sqrt(Px * Px + Py * Py + Pz * Pz)
            val rx = Px / Plength
            val ry = Py / Plength
            val rz = Pz / Plength
            val Sa = sin(angle)
            val Ca = cos(angle)
            val Va = 1.0 - Ca
            val rot: Mat44 = MatrixFactory.Identity()
            rot[0, 0] = rx * rx * Va + Ca
            rot[0, 1] = rx * ry * Va - rz * Sa
            rot[0, 2] = rx * rz * Va + ry * Sa
            rot[1, 0] = rx * ry * Va + rz * Sa
            rot[1, 1] = ry * ry * Va + Ca
            rot[1, 2] = ry * rz * Va - rx * Sa
            rot[2, 0] = rx * rz * Va - ry * Sa
            rot[2, 1] = ry * rz * Va + rx * Sa
            rot[2, 2] = rz * rz * Va + Ca
            var res: Mat44 = Mat44.multiply(MatrixFactory.Translate(-Px, -Py, -Pz), rot)
            res = Mat44.multiply(res, MatrixFactory.Translate(+Px, +Py, +Pz))
            return res
        }
    }
}