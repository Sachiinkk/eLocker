package com.example.elocker.ui.screens
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import java.io.File
import java.io.FileOutputStream

@Composable
fun PdfViewScreen(base64Pdf: String) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // PDF decoding and rendering
    LaunchedEffect(base64Pdf) {
        try {
            val bytes = Base64.decode(base64Pdf, Base64.DEFAULT)
            val file = File.createTempFile("temp", ".pdf", context.cacheDir)
            FileOutputStream(file).use { it.write(bytes) }

            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)
            val page = pdfRenderer.openPage(0)

            val bmp = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmap = bmp

            page.close()
            pdfRenderer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    bitmap?.let {
        ZoomableImage(it)
    }
}

@Composable
fun ZoomableImage(bitmap: Bitmap) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Zoomable PDF Page",
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    )
}
