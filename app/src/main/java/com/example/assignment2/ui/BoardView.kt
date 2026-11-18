package com.example.assignment2.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.assignment2.R
import com.example.assignment2.model.Tile
import kotlin.math.PI
import kotlin.math.sin

class BoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var tiles: List<Tile> = emptyList()
    private var playerPosition: Int = 0
    private var animatedPawnPos: Float = 0f

    private val gridCount = 5
    private val tileRects = Array(16) { RectF() }

    // Money Magic Stage Outlines & Paints
    private val thickOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#241C15")
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val curtainBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C98838")
    }

    private val curtainFoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B27329")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val parchmentFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FAF3DF")
        style = Paint.Style.FILL
    }

    private val centerBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFDF5")
        style = Paint.Style.FILL
    }

    private val tileTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#241C15")
        textSize = 22f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val logoTilePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val logoTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 32f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private var pawnDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_pawn)

    // Map board index (0..15) to grid (row, col)
    private val tileGridCoords = arrayOf(
        Pair(4, 4), // 0: START
        Pair(4, 3), // 1: Food
        Pair(4, 2), // 2: Transport
        Pair(4, 1), // 3: Quiz Medical
        Pair(4, 0), // 4: Rent
        Pair(3, 0), // 5: Bills
        Pair(2, 0), // 6: Quiz Car Repair
        Pair(1, 0), // 7: Savings
        Pair(0, 0), // 8: Quiz Mobile Phone
        Pair(0, 1), // 9: Education
        Pair(0, 2), // 10: Entertainment
        Pair(0, 3), // 11: Quiz Discount
        Pair(0, 4), // 12: Quiz Online Shop
        Pair(1, 4), // 13: Quiz Edu Fund
        Pair(2, 4), // 14: Emergency Savings
        Pair(3, 4)  // 15: Quiz Electricity
    )

    fun setTiles(tilesList: List<Tile>) {
        this.tiles = tilesList
        invalidate()
    }

    fun setPlayerPosition(position: Int, animate: Boolean = true) {
        val startPos = (animatedPawnPos % 16f + 16f) % 16f
        var targetPos = position.toFloat()

        if (targetPos < startPos) {
            targetPos += 16f
        }

        if (animate) {
            com.example.assignment2.sound.SoundManager.playPawnStepSound()
            val duration = if (com.example.assignment2.sound.SoundManager.isFastSpeed) 300L else 500L
            val animator = ValueAnimator.ofFloat(startPos, targetPos)
            animator.duration = duration
            animator.interpolator = DecelerateInterpolator()
            animator.addUpdateListener { anim ->
                animatedPawnPos = (anim.animatedValue as Float) % 16f
                invalidate()
            }
            animator.start()
        } else {
            animatedPawnPos = targetPos % 16f
            invalidate()
        }
        this.playerPosition = position
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (w < h && w > 0) w else if (h > 0) h else w
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        if (width <= 0 || height <= 0) return

        // Draw Stage Curtain Background & Vertical Folds
        val boardRect = RectF(0f, 0f, width, height)
        canvas.drawRoundRect(boardRect, 16f, 16f, curtainBgPaint)

        // Draw Stage Curtain Folds
        val foldSpacing = width / 18f
        for (f in 1..17) {
            val fx = f * foldSpacing
            canvas.drawLine(fx, 0f, fx, height, curtainFoldPaint)
        }
        canvas.drawRoundRect(boardRect, 16f, 16f, thickOutlinePaint)

        val cellSize = width / gridCount

        // Draw 16 Parchment Tiles
        for (i in 0 until 16) {
            val (row, col) = tileGridCoords[i]
            val left = col * cellSize
            val top = row * cellSize
            tileRects[i].set(left, top, left + cellSize, top + cellSize)

            // Draw Parchment Cream Tile Fill
            canvas.drawRoundRect(tileRects[i], 8f, 8f, parchmentFillPaint)
            canvas.drawRoundRect(tileRects[i], 8f, 8f, thickOutlinePaint)

            // Draw Category Color Header Strip
            val tile = tiles.getOrNull(i)
            if (tile != null) {
                val catColor = ContextCompat.getColor(context, tile.categoryColorRes)
                val stripPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = catColor
                    style = Paint.Style.FILL
                }
                val stripHeight = cellSize * 0.24f
                val stripRect = RectF(left + 3f, top + 3f, left + cellSize - 3f, top + stripHeight)
                canvas.drawRect(stripRect, stripPaint)
                canvas.drawRect(stripRect, thickOutlinePaint)

                val topHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = stripHeight * 0.58f
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                }

                // Draw Wording at the TOP of the box inside header strip for all tiles
                val titleText = if (tile.type == com.example.assignment2.model.TileType.QUIZ) "QUIZ" else tile.name
                if (titleText.length > 9) {
                    topHeaderPaint.textSize = stripHeight * 0.44f
                }
                canvas.drawText(titleText, left + cellSize / 2f, top + stripHeight * 0.70f, topHeaderPaint)

                // Draw Tile Icon in middle/center
                val drawable = ContextCompat.getDrawable(context, tile.iconRes)
                if (drawable != null) {
                    val iconSize = (cellSize * 0.46f).toInt()
                    val iconLeft = (left + (cellSize - iconSize) / 2).toInt()
                    val iconTop = (top + stripHeight + (cellSize - stripHeight - iconSize) / 2).toInt()
                    drawable.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize)
                    drawable.draw(canvas)
                }
            }
        }

        // Draw Central Stage Theater Region (3x3 inner region)
        val centerLeft = cellSize
        val centerTop = cellSize
        val centerRight = cellSize * 4
        val centerBottom = cellSize * 4
        val centerRect = RectF(centerLeft, centerTop, centerRight, centerBottom)
        canvas.drawRoundRect(centerRect, 14f, 14f, centerBgPaint)
        canvas.drawRoundRect(centerRect, 14f, 14f, thickOutlinePaint)

        // Draw "FINOPOLY" Offset Paper Signboard
        val centerX = width / 2f
        val centerY = height / 2f

        // "FINO" Row Paper Tiles
        val finoLetters = arrayOf("F", "I", "N", "O")
        val finoColors = intArrayOf(
            Color.parseColor("#1D8B8B"), // Teal
            Color.parseColor("#FAF3DF"), // Parchment
            Color.parseColor("#1D8B8B"), // Teal
            Color.parseColor("#FAF3DF")  // Parchment
        )
        val letterWidth = cellSize * 0.62f
        val letterHeight = cellSize * 0.58f
        val totalFinoW = finoLetters.size * letterWidth
        val startFinoX = centerX - totalFinoW / 2f + letterWidth / 2f
        val finoY = centerY - cellSize * 0.45f

        for (m in finoLetters.indices) {
            val lx = startFinoX + m * letterWidth
            val rotAngle = if (m % 2 == 0) -3f else 3f
            canvas.save()
            canvas.rotate(rotAngle, lx, finoY)

            val tileBox = RectF(lx - letterWidth / 2f + 2f, finoY - letterHeight / 2f, lx + letterWidth / 2f - 2f, finoY + letterHeight / 2f)
            logoTilePaint.color = finoColors[m]
            canvas.drawRoundRect(tileBox, 6f, 6f, logoTilePaint)
            canvas.drawRoundRect(tileBox, 6f, 6f, thickOutlinePaint)

            logoTextPaint.color = if (finoColors[m] == Color.parseColor("#FAF3DF")) Color.parseColor("#1D8B8B") else Color.parseColor("#E5A853")
            logoTextPaint.textSize = letterHeight * 0.65f
            canvas.drawText(finoLetters[m], lx, finoY + letterHeight * 0.22f, logoTextPaint)

            canvas.restore()
        }

        // "POLY" Row Paper Tiles
        val polyLetters = arrayOf("P", "O", "L", "Y")
        val polyY = centerY + cellSize * 0.25f
        val startPolyX = centerX - (polyLetters.size * letterWidth) / 2f + letterWidth / 2f

        for (m in polyLetters.indices) {
            val lx = startPolyX + m * letterWidth
            val rotAngle = if (m % 2 == 0) 3f else -3f
            canvas.save()
            canvas.rotate(rotAngle, lx, polyY)

            val tileBox = RectF(lx - letterWidth / 2f + 2f, polyY - letterHeight / 2f, lx + letterWidth / 2f - 2f, polyY + letterHeight / 2f)
            logoTilePaint.color = finoColors[(m + 1) % finoColors.size]
            canvas.drawRoundRect(tileBox, 6f, 6f, logoTilePaint)
            canvas.drawRoundRect(tileBox, 6f, 6f, thickOutlinePaint)

            logoTextPaint.color = if (logoTilePaint.color == Color.parseColor("#FAF3DF")) Color.parseColor("#241C15") else Color.parseColor("#E5A853")
            logoTextPaint.textSize = letterHeight * 0.65f
            canvas.drawText(polyLetters[m], lx, polyY + letterHeight * 0.22f, logoTextPaint)

            canvas.restore()
        }

        // Calculate Enzo Magician Hop Animation around the 16-tile Monopoly loop
        val normPos = (animatedPawnPos % 16f + 16f) % 16f
        val currentTileIdx = normPos.toInt()
        val nextTileIdx = (currentTileIdx + 1) % 16
        val fraction = normPos - currentTileIdx

        val curRect = tileRects[currentTileIdx]
        val nextRect = tileRects[nextTileIdx]

        val pawnX = curRect.centerX() + (nextRect.centerX() - curRect.centerX()) * fraction
        var pawnY = curRect.centerY() + (nextRect.centerY() - curRect.centerY()) * fraction

        // Bouncy Hop Arc
        if (fraction > 0f) {
            val bounceHeight = cellSize * 0.40f
            pawnY -= (sin(fraction * PI) * bounceHeight).toFloat()
        }

        val pawnSize = (cellSize * 0.68f).toInt()
        val pLeft = (pawnX - pawnSize / 2).toInt()
        val pTop = (pawnY - pawnSize / 2).toInt()

        // Shadow under Enzo Magician
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#50000000")
            style = Paint.Style.FILL
        }
        canvas.drawOval(pawnX - pawnSize * 0.35f, curRect.centerY() + pawnSize * 0.22f, pawnX + pawnSize * 0.35f, curRect.centerY() + pawnSize * 0.38f, shadowPaint)

        // Draw Enzo Magician
        pawnDrawable?.setBounds(pLeft, pTop, pLeft + pawnSize, pTop + pawnSize)
        pawnDrawable?.draw(canvas)
    }
}