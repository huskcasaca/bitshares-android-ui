package kdenticon

import kotlin.math.floor

class Graphics(renderer: Renderer) {

    private val _renderer = renderer
    var noTransform = Transform.noTransform()

    fun addPolygon(points: List<Point>, invert: Boolean = false) {
        val transform = this.noTransform
        val transformedPoints = ArrayList<Point>()
        val i = if (invert) points.size - 1 else 0
        if (!invert) {
            for (x in i until points.size) {
                transformedPoints.add(
                        transform.transformPoint(
                                points[x].x,
                                points[x].y
                        )
                );
            }
        } else {
            for (x in i downTo 0) {
                transformedPoints.add(
                        transform.transformPoint(
                                points[x].x,
                                points[x].y
                        )
                );
            }
        }
        this._renderer.addPolygon(transformedPoints)
    }

    fun addCircle(x: Float, y: Float, size: Float, invert: Boolean = false) {
        val p = this.noTransform.transformPoint(x, y, size, size)
        this._renderer.addCircle(p, size, invert)
    }

    fun addRectangle(x: Float, y: Float, w: Float, h: Float, invert: Boolean = false) {
        this.addPolygon(
                listOf(
                        Point(x, y),
                        Point(x + w, y),
                        Point(x + w, y + h),
                        Point(x, y + h)
                ),
                invert
        )
    }

    fun addTriangle(x: Float, y: Float, w: Float, h: Float, r: Float, invert: Boolean = false) {
        val points = ArrayList<Point>(listOf(
            Point(x + w, y),
            Point(x + w, y + h),
            Point(x, y + h),
            Point(x, y)
        ))
        //original splice method did not use point objects, and needed to delete two elements
        points.removeAt(floor(r).toInt() % 4);
        this.addPolygon(points, invert);
    }

    fun addRhombus(x: Float, y: Float, w: Float, h: Float, invert: Boolean = false) {
        this.addPolygon(
                listOf(
                        Point(x + w / 2f, y),
                        Point(x + w, y + h / 2f),
                        Point(x + w / 2f, y + h),
                        Point(x, y + h / 2f)
                ),
                invert
        )
    }
}